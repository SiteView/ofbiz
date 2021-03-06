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
package org.ofbiz.minilang.method.ifops;

import java.util.Collections;
import java.util.List;

import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.artifact.ArtifactInfoContext;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;if-not-empty&gt; element.
 */
public final class IfNotEmpty extends MethodOperation {

    private final List<MethodOperation> elseSubOps;
    private final FlexibleMapAccessor<Object> fieldFma;
    private final List<MethodOperation> subOps;

    public IfNotEmpty(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        this.subOps = Collections.unmodifiableList(SimpleMethod.readOperations(element, simpleMethod));
        Element elseElement = UtilXml.firstChildElement(element, "else");
        if (elseElement != null) {
            this.elseSubOps = Collections.unmodifiableList(SimpleMethod.readOperations(elseElement, simpleMethod));
        } else {
            this.elseSubOps = null;
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Object fieldVal = fieldFma.get(methodContext.getEnvMap());
        if (!ObjectType.isEmpty(fieldVal)) {
            return SimpleMethod.runSubOps(subOps, methodContext);
        } else {
            if (elseSubOps != null) {
                return SimpleMethod.runSubOps(elseSubOps, methodContext);
            } else {
                return true;
            }
        }
    }

    @Override
    public String expandedString(MethodContext methodContext) {
        return FlexibleStringExpander.expandString(toString(), methodContext.getEnvMap());
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        for (MethodOperation method : this.subOps) {
            method.gatherArtifactInfo(aic);
        }
        if (this.elseSubOps != null) {
            for (MethodOperation method : this.elseSubOps) {
                method.gatherArtifactInfo(aic);
            }
        }
    }

    @Override
    public String rawString() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<if-not-empty ");
        sb.append("field=\"").append(this.fieldFma).append("\"/>");
        return sb.toString();
    }

    /**
     * A &lt;if-not-empty&gt; element factory. 
     */
    public static final class IfNotEmptyFactory implements Factory<IfNotEmpty> {
        @Override
        public IfNotEmpty createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new IfNotEmpty(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "if-not-empty";
        }
    }
}
