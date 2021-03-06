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
package org.ofbiz.minilang.method.entityops;

import org.ofbiz.minilang.artifact.ArtifactInfoContext;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.finder.PrimaryKeyFinder;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.MiniLangValidate;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;entity-one&gt; element.
 */
public final class EntityOne extends MethodOperation {

    public static final String module = EntityOne.class.getName();

    private final PrimaryKeyFinder finder;

    public EntityOne(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "entity-name", "use-cache", "auto-field-map", "value-field");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "entity-name", "value-field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field");
            MiniLangValidate.childElements(simpleMethod, element, "field-map", "select-field");
        }
        this.finder = new PrimaryKeyFinder(element);
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        try {
            Delegator delegator = methodContext.getDelegator();
            this.finder.runFind(methodContext.getEnvMap(), delegator);
        } catch (GeneralException e) {
            String errMsg = "Exception thrown while performing entity find: " + e.getMessage();
            Debug.logWarning(e, errMsg, module);
            simpleMethod.addErrorMessage(methodContext, errMsg);
            return false;
        }
        return true;
    }

    @Override
    public String expandedString(MethodContext methodContext) {
        return FlexibleStringExpander.expandString(toString(), methodContext.getEnvMap());
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addEntityName(this.finder.getEntityName());
    }

    @Override
    public String rawString() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<entity-one ");
        sb.append("entity-name=\"").append(this.finder.getEntityName()).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;entity-one&gt; element.
     */
    public static final class EntityOneFactory implements Factory<EntityOne> {
        @Override
        public EntityOne createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new EntityOne(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "entity-one";
        }
    }
}
