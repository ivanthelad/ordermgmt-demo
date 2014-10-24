
package org.jboss.rtgov.actvity;
  /* 2012-4 Red Hat Inc. and/or its affiliates and other contributors.
         *
         * Licensed under the Apache License, Version 2.0 (the "License");
         * you may not use this file except in compliance with the License.
         * You may obtain a copy of the License at
         *
         * http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         */

/**
 * User: imk@redhat.com
 * Date: 10/07/14
 * Time: 13:23
 */

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.analytics.service.ResponseTime;
import org.overlord.rtgov.analytics.situation.Situation;


public class Util {
    public static ResponseTime createResponseTime(RequestReceived req, ResponseSent responseSent) {
        ResponseTime rt = new ResponseTime();
        ;
        rt.setRequestId(ActivityTypeId.createId(req));
        rt.setResponseId(ActivityTypeId.createId(responseSent));
        rt.setServiceType(req.getServiceType());
        rt.setInterface(req.getInterface());
        rt.setOperation(req.getOperation());
        rt.setFault(responseSent.getFault());
        rt.setProperties(req.getProperties());
        // todo set the context for the response time object
        //  rt.setContext(req.getContext());
        long responseTime = responseSent.getTimestamp() - req.getTimestamp();
        rt.setAverage(responseTime);
        return rt;
    }

    public static Situation createSituationRes(RequestReceived rt) {

        Situation situation=new Situation();
        situation.setDescription("Excessive number of requests for service  ["+rt.getServiceType()+"] " );


        situation.setSubject(Situation.createSubject(rt.getServiceType(), rt.getOperation(),
                rt.getFault()));
        situation.setTimestamp(System.currentTimeMillis());

        situation.getProperties().putAll(rt.getProperties());



        situation.getContext().addAll(rt.getContext());

        String serviceName=rt.getServiceType();

        if (serviceName.startsWith("{")) {
            serviceName = javax.xml.namespace.QName.valueOf(serviceName).getLocalPart();
        }
        return situation;

    }
    public static String getConversation(java.util.Set<Context>  activityType){
        for (Context c : activityType) {
            if (c.getType() == Context.Type.Conversation) {
                return c.getValue();

            }
        }
        return null;
    }
    public static Situation createSituation(ResponseTime rt) {

        Situation situation=new Situation();

        situation.setType("SLA Violation");
        situation.setSubject(Situation.createSubject(rt.getServiceType(), rt.getOperation(),
                rt.getFault()));
        situation.setTimestamp(System.currentTimeMillis());

        situation.getProperties().putAll(rt.getProperties());

        if (rt.getRequestId() != null) {
            situation.getActivityTypeIds().add(rt.getRequestId());
        }
        if (rt.getResponseId() != null) {
            situation.getActivityTypeIds().add(rt.getResponseId());
        }

        situation.getContext().addAll(rt.getContext());

        String serviceName=rt.getServiceType();

        if (serviceName.startsWith("{")) {
            serviceName = javax.xml.namespace.QName.valueOf(serviceName).getLocalPart();
        }
        return situation;

    }


}
