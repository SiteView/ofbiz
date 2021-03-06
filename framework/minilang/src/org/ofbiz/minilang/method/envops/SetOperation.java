/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.minilang.method.envops;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.Scriptlet;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.MiniLangUtil;
import org.ofbiz.minilang.MiniLangValidate;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Assigns a field from an expression or script, or from a constant value. Also supports a default value and type conversion.
 */
public final class SetOperation extends MethodOperation {

    public static final String module = SetOperation.class.getName();

    // This method is needed only during the v1 to v2 transition
    private static boolean autoCorrect(Element element) {
        boolean elementModified = false;
        // Correct deprecated default-value attribute
        String defaultAttr = element.getAttribute("default-value");
        if (defaultAttr.length() > 0) {
            element.setAttribute("default", defaultAttr);
            element.removeAttribute("default-value");
            elementModified = true;
        }
        // Correct deprecated from-field attribute
        String fromAttr = element.getAttribute("from-field");
        if (fromAttr.length() > 0) {
            element.setAttribute("from", fromAttr);
            element.removeAttribute("from-field");
            elementModified = true;
        }
        // Correct value attribute expression that belongs in from attribute
        String valueAttr = element.getAttribute("value").trim();
        if (valueAttr.startsWith("${") && valueAttr.endsWith("}")) {
            valueAttr = valueAttr.substring(2, valueAttr.length() - 1);
            if (!valueAttr.contains("${")) {
                element.setAttribute("from", valueAttr);
                element.removeAttribute("value");
                elementModified = true;
            }
        }
        return elementModified;
    }

    private final FlexibleStringExpander defaultFse;
    private final FlexibleMapAccessor<Object> fieldFma;
    private final FlexibleMapAccessor<Object> fromFma;
    private final Scriptlet scriptlet;
    private final boolean setIfEmpty;
    private final boolean setIfNull;
    private final Class<?> targetClass;
    private final String type;
    private final FlexibleStringExpander valueFse;

    public SetOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            /*
            MiniLangValidate.deprecatedAttribute(simpleMethod, element, "from-field", "replace with \"from\"");
            MiniLangValidate.deprecatedAttribute(simpleMethod, element, "default-value", "replace with \"default\"");
            */
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "from-field", "from", "value", "default-value", "default", "type", "set-if-null", "set-if-empty");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field");
            MiniLangValidate.requireAnyAttribute(simpleMethod, element, "from-field", "from", "value");
            MiniLangValidate.constantPlusExpressionAttributes(simpleMethod, element, "value");
            MiniLangValidate.constantAttributes(simpleMethod, element, "type", "set-if-null", "set-if-empty");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        boolean elementModified = autoCorrect(element);
        if (elementModified && MiniLangUtil.autoCorrectOn()) {
            MiniLangUtil.flagDocumentAsCorrected(element);
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        String fromAttribute = element.getAttribute("from");
        if (MiniLangUtil.containsScript(fromAttribute)) {
            this.scriptlet = new Scriptlet(StringUtil.convertOperatorSubstitutions(fromAttribute));
            this.fromFma = FlexibleMapAccessor.getInstance(null);
        } else {
            this.scriptlet = null;
            this.fromFma = FlexibleMapAccessor.getInstance(fromAttribute);
        }
        this.valueFse = FlexibleStringExpander.getInstance(element.getAttribute("value"));
        this.defaultFse = FlexibleStringExpander.getInstance(element.getAttribute("default"));
        this.type = element.getAttribute("type");
        Class<?> targetClass = null;
        if (!this.type.isEmpty()) {
            try {
                targetClass = ObjectType.loadClass(this.type);
            } catch (ClassNotFoundException e) {
                MiniLangValidate.handleError("Invalid type " + this.type, simpleMethod, element);
            }
        }
        this.targetClass = targetClass;
        this.setIfNull = "true".equals(element.getAttribute("set-if-null")); // default to false, anything but true is false
        this.setIfEmpty = !"false".equals(element.getAttribute("set-if-empty")); // default to true, anything but false is true
        if (!this.fromFma.isEmpty() && !this.valueFse.isEmpty()) {
            throw new IllegalArgumentException("Cannot include both a from attribute and a value attribute in a <set> element.");
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Object newValue = null;
        if (this.scriptlet != null) {
            try {
                newValue = this.scriptlet.executeScript(methodContext.getEnvMap());
            } catch (Exception exc) {
                Debug.logWarning(exc, "Error evaluating scriptlet [" + this.scriptlet + "]: " + exc, module);
            }
        } else if (!this.fromFma.isEmpty()) {
            newValue = this.fromFma.get(methodContext.getEnvMap());
            if (Debug.verboseOn())
                Debug.logVerbose("In screen getting value for field from [" + this.fromFma.toString() + "]: " + newValue, module);
        } else if (!this.valueFse.isEmpty()) {
            newValue = this.valueFse.expand(methodContext.getEnvMap());
        }
        // If newValue is still empty, use the default value
        if (ObjectType.isEmpty(newValue) && !this.defaultFse.isEmpty()) {
            newValue = this.defaultFse.expand(methodContext.getEnvMap());
        }
        if (!setIfNull && newValue == null) {
            if (Debug.verboseOn())
                Debug.logVerbose("Field value not found (null) with name [" + fromFma + "] and value [" + valueFse + "], and there was not default value, not setting field", module);
            return true;
        }
        if (!setIfEmpty && ObjectType.isEmpty(newValue)) {
            if (Debug.verboseOn())
                Debug.logVerbose("Field value not found (empty) with name [" + fromFma + "] and value [" + valueFse + "], and there was not default value, not setting field", module);
            return true;
        }
        if (this.type.length() > 0) {
            if ("NewMap".equals(this.type)) {
                newValue = FastMap.newInstance();
            } else if ("NewList".equals(this.type)) {
                newValue = FastList.newInstance();
            } else {
                try {
                    Class<?> targetClass = this.targetClass;
                    if (targetClass == null) {
                        targetClass = MiniLangUtil.getObjectClassForConversion(newValue);
                    }
                    newValue = MiniLangUtil.convertType(newValue, targetClass, methodContext.getLocale(), methodContext.getTimeZone(), null);
                } catch (Exception e) {
                    String errMsg = "Could not convert field value for the field: [" + this.fieldFma.toString() + "] to the [" + this.type + "] type for the value [" + newValue + "]: " + e.toString();
                    Debug.logWarning(e, errMsg, module);
                    this.simpleMethod.addErrorMessage(methodContext, errMsg);
                    return false;
                }
            }
        }
        if (Debug.verboseOn())
            Debug.logVerbose("In screen setting field [" + this.fieldFma.toString() + "] to value: " + newValue, module);
        this.fieldFma.put(methodContext.getEnvMap(), newValue);
        return true;
    }

    @Override
    public String expandedString(MethodContext methodContext) {
        return FlexibleStringExpander.expandString(toString(), methodContext.getEnvMap());
    }

    @Override
    public String rawString() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<set ");
        if (!this.fieldFma.isEmpty()) {
            sb.append("field=\"").append(this.fieldFma).append("\" ");
        }
        if (!this.fromFma.isEmpty()) {
            sb.append("from=\"").append(this.fromFma).append("\" ");
        }
        if (this.scriptlet != null) {
            sb.append("from=\"").append(this.scriptlet).append("\" ");
        }
        if (!this.valueFse.isEmpty()) {
            sb.append("value=\"").append(this.valueFse).append("\" ");
        }
        if (!this.defaultFse.isEmpty()) {
            sb.append("default=\"").append(this.defaultFse).append("\" ");
        }
        if (this.type.length() > 0) {
            sb.append("type=\"").append(this.type).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    public static final class SetOperationFactory implements Factory<SetOperation> {
        public SetOperation createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new SetOperation(element, simpleMethod);
        }

        public String getName() {
            return "set";
        }
    }
}
