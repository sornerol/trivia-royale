package com.triviaroyale.service

import com.amazonaws.services.s3.AmazonS3
import com.triviaroyale.data.Question
import groovy.transform.CompileStatic

@CompileStatic
class QuestionService {

    final AmazonS3 s3

    QuestionService(AmazonS3 s3) {
        this.s3 = s3
    }

}
