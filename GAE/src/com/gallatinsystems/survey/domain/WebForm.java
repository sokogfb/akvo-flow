/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WebForm {

    public static Set<String> unsupportedQuestionTypes(){
        Set<String> unsupportedTypes = new HashSet<String>();
        unsupportedTypes.add(Question.Type.CASCADE.toString());
        unsupportedTypes.add(Question.Type.GEOSHAPE.toString());
        unsupportedTypes.add(Question.Type.SIGNATURE.toString());
        unsupportedTypes.add(Question.Type.CADDISFLY.toString());
        return unsupportedTypes;
    }

    public static boolean validWebForm(final List<Question> questions){
        
        List<Question> validQuestions = questions.stream().filter(i -> !unsupportedQuestionTypes().contains(i.getType().toString())).collect(Collectors.toList());

        return validQuestions.size() == questions.size();
    }

}