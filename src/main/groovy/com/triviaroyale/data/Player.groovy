package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.util.AppState
import groovy.transform.CompileStatic

@CompileStatic
@DynamoDBTable(tableName = DynamoDBConstants.TABLE_NAME)
class Player implements Cloneable {

    @DynamoDBHashKey(attributeName = DynamoDBConstants.HASH_KEY)
    String alexaId

    //Our table requires a range (sort) key, but we don't really have a need for sorting players.
    @DynamoDBRangeKey(attributeName = DynamoDBConstants.RANGE_KEY)
    String rk = 'METADATA'

    @DynamoDBAttribute(attributeName = 'quizCompletion')
    Map<String, String> quizCompletion

    @DynamoDBAttribute
    int secondChancesPurchased = 0

    @DynamoDBAttribute
    int secondChancesConsumed = 0

    @DynamoDBAttribute
    String ispSessionId

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    @DynamoDBAttribute
    AppState ispAppState

    @Override
    Object clone() throws CloneNotSupportedException {
        (Player) super.clone()
    }

    @Override
    boolean equals(Object obj) {
        if (!obj) {
            return false
        }
        if (this.class != obj.class) {
            return false
        }
        Player other = obj as Player

        Objects.equals(this.alexaId, other.alexaId) &&
                Objects.equals(rk, other.rk) &&
                Objects.equals(quizCompletion, other.quizCompletion) &&
                Objects.equals(secondChancesPurchased, other.secondChancesPurchased) &&
                Objects.equals(secondChancesConsumed, other.secondChancesConsumed) &&
                Objects.equals(ispSessionId, other.ispSessionId) &&
                Objects.equals(ispAppState, other.ispAppState)
    }

    @Override
    int hashCode() {
        Objects.hash(rk,
                quizCompletion,
                secondChancesPurchased,
                secondChancesConsumed,
                ispSessionId,
                ispAppState)
    }

}
