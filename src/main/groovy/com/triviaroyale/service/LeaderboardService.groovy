package com.triviaroyale.service

import static java.time.DayOfWeek.SUNDAY
import static java.time.temporal.TemporalAdjusters.*

import groovy.transform.CompileStatic
import groovy.util.logging.Log
import redis.clients.jedis.Jedis

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@CompileStatic
@Log
class LeaderboardService {

    public static final Map<Integer, Integer> PLACE_REWARDS = [
            1 : 25,
            2 : 15,
            3 : 10,
            4 : 7,
            5 : 6,
            6 : 5,
            7 : 4,
            8 : 3,
            9 : 2,
            10: 1,
    ]

    public static final String LEADERBOARD_KEY_PREFIX = 'LEADERBOARD_'
    public static final String SEASON_DATE_FORMAT = 'YYYY_MM_dd'
    public static final String UTC = 'UTC'

    public static final int NO_PLACE = 0

    Jedis jedis

    LeaderboardService(Jedis jedis) {
        this.jedis = jedis
    }

    static LocalDate getSeason(LocalDate date) {
        LocalDate seasonStart = date.with(previousOrSame(SUNDAY))
        seasonStart
    }

    static String getSeasonKey(LocalDate date) {
        LocalDate seasonStart = getSeason(date)
        LEADERBOARD_KEY_PREFIX + seasonStart.format(DateTimeFormatter.ofPattern(SEASON_DATE_FORMAT))
    }

    static String getCurrentSeasonKey() {
        getSeasonKey(LocalDate.now(ZoneId.of(UTC)))
    }

    static String getPreviousSeasonKey() {
        LocalDate currentSeason = getSeason(LocalDate.now(ZoneId.of(UTC)))
        LocalDate previousSeason = currentSeason.with(previous(SUNDAY))
        getSeasonKey(previousSeason)
    }

    static long getSeasonExpireTime() {
        LocalDate currentSeason = getSeason(LocalDate.now(ZoneId.of(UTC)))
        LocalDate nextSeason = currentSeason.with(next(SUNDAY))
        LocalDateTime expireAt = nextSeason.with(next(SUNDAY)).atStartOfDay()
        expireAt.atZone(ZoneId.of(UTC)).toInstant().toEpochMilli()
    }

    int getRank(String leaderboard, String playerId) {
        Double playerScore = jedis.zscore(leaderboard, playerId)
        if (playerScore == null) {
            return NO_PLACE
        }
        String firstPlayerWithScore = jedis.zrangeByScore(leaderboard, playerScore, playerScore, 0, 1).first()
        (jedis.zrank(leaderboard, firstPlayerWithScore) + 1).toInteger()
    }

    int getRankForCurrentSeason(String playerId) {
        getRank(currentSeasonKey, playerId)
    }

    int getRankForPreviousSeason(String playerId) {
        getRank(previousSeasonKey, playerId)
    }

    int addPoints(String playerId, int pointsToAdd) {
        String leaderboard = currentSeasonKey
        Integer currentScore = jedis.zscore(leaderboard, playerId) as Integer
        if (currentScore == null) {
            currentScore = 0
        }
        currentScore += pointsToAdd
        jedis.zadd(leaderboard, currentScore, playerId)
        jedis.expireAt(leaderboard, seasonExpireTime)
        getRankForCurrentSeason(playerId)
    }

}
