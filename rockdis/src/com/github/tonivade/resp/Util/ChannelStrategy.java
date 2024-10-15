package com.github.tonivade.resp.Util;

import com.github.tonivade.resp.RespServer;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kkw
 *
 */

public class ChannelStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelStrategy.class);
    private static Strategy channelStrategy = Strategy.NIO;

    public enum Strategy {
        EPOLL, KQUEUE, NIO
    }

    public static Strategy getChannelStrategy() {
        searchChannelStrategy();
        return channelStrategy;
    }

    public static void searchChannelStrategy() {
        boolean isEpoll = true;
        boolean isKQueue = true;

        try {
            Epoll.ensureAvailability();
        } catch (UnsatisfiedLinkError error) {
            isEpoll = false;
            LOGGER.debug("Cannot use epoll Channel Strategy.");
        }

        try {
            KQueue.ensureAvailability();
        } catch (UnsatisfiedLinkError error) {
            isKQueue = false;
            LOGGER.debug("Cannot use kqueue Channel Strategy.");
        }

        if (isEpoll && isKQueue) {
            LOGGER.error("You cannot use epoll and kqueue at the same time.");

        } else if (isEpoll) {
            LOGGER.debug("You should use EPOLL Strategy.");
            channelStrategy = Strategy.EPOLL;
        } else if (isKQueue) {
            LOGGER.debug("You should use KQUEUE Strategy.");
            channelStrategy = Strategy.KQUEUE;
        } else {
            LOGGER.debug("You should use NIO Strategy.");
            channelStrategy = Strategy.NIO;
        }
    }
}
