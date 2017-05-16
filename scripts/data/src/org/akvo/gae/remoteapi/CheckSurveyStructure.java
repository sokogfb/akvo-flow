/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.gae.remoteapi;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/*
 * - Checks that all questions are in the questiongroup of their survey
 */
public class CheckSurveyStructure implements Process {

    //    private static String ERR_MSG = "Unable to hide SurveyedLocale [%s], reason: %s";

    @Override
    public void execute(DatastoreService ds, String[] args) throws Exception {


        System.out.println("Processing Question Groups");

	Map<Long,Long>qgToSurvey = new HashMap<>();
	
        final Query group_q = new Query("QuestionGroup");
        final PreparedQuery group_pq = ds.prepare(group_q);

        for (Entity g : group_pq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionGroupId = (Long) g.getProperty("id");
            Long questionGroupSurvey = (Long) g.getProperty("surveyId");
	    qgToSurvey.put(questionGroupId, questionGroupSurvey);

        }

        System.out.println("Processing Questions");

        final Query qq = new Query("Question");
        final PreparedQuery qpq = ds.prepare(qq);

        for (Entity sl : qpq.asIterable(FetchOptions.Builder.withChunkSize(500))) {

            Long questionGroup = (Long) sl.getProperty("questionGroup");

	    if (questionGroup == null) { //check for no qg
		String id = (String) sl.getProperty("id");
		System.out.println("ERR: Question not in a group: " + id);
	    } else { //TODO: check for wrong survey/qg
		
		//		if (! questionGroup.equals())
		//    {}
	    }
	}


    }
}
