/*
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
 */

import org.ofbiz.entity.GenericValue;

shipmentId = parameters.shipmentId;
shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);

context.shipmentIdPar = shipment.shipmentId;
context.date = new Date();
Double fixedAssetTime = new Double(0);

if (shipment) {
    shipmentPlans = delegator.findByAnd("OrderShipment", [shipmentId : shipmentId], null, false);
    shipmentPlans.each { shipmentPlan ->
        productionRuns = delegator.findByAnd("WorkOrderItemFulfillment", [orderId : shipmentPlan.orderId, orderItemSeqId : shipmentPlan.orderItemSeqId] , ["workEffortId"], false); // TODO: add shipmentId
        if (productionRuns) {
            productionRuns.each { productionRun ->
                productionRunProduct = [:];
                productionRunProducts = delegator.findByAnd("WorkEffortGoodStandard", [workEffortId : productionRun.workEffortId , workEffortGoodStdTypeId : "PRUN_PROD_DELIV", statusId : "WEGS_CREATED"], null, false);
                if (productionRunProducts) {
                    productionRunProduct = ((GenericValue)productionRunProducts.get(0)).getRelatedOne("Product");
                }
                tasks = delegator.findByAnd("WorkEffort", [workEffortParentId : productionRun.workEffortId, workEffortTypeId : "PROD_ORDER_TASK"], null, false);
                tasks.each { task ->
                    record = [:];
                    record.productId = productionRunProduct.productId;
                    record.productName = productionRunProduct.internalName;
                    record.fixedAssetId = task.fixedAssetId;
                    record.priority = task.getLong("priority");
                    record.workEffortId = productionRun.workEffortId;
                    record.taskId = task.workEffortId;
                    record.taskName = task.workEffortName;
                    record.taskDescription = task.description;
                    record.taskEstimatedTime = task.getDouble("estimatedMilliSeconds");
                    record.taskEstimatedSetup = task.getDouble("estimatedSetupMillis");
                    records.add(record);
                    fixedAssetTime = fixedAssetTime + task.getDouble("estimatedMilliSeconds");
                }
            }
        }
    }
    context.fixedAssetTime = fixedAssetTime;
    context.records = records;

    // check permission
    hasPermission = false;
    if (security.hasEntityPermission("MANUFACTURING", "_VIEW", session)) {
        hasPermission = true;
    }
    context.hasPermission = hasPermission;
}

return "success";
