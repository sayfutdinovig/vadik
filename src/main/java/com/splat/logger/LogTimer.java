package com.splat.logger;

import com.splat.servlets.AmountServlet;
import org.slf4j.Logger;

import java.util.TimerTask;

import static org.slf4j.LoggerFactory.getLogger;

/**
 Описываем таймер для вывода в лог статистической информации.
 При очередном выводе счетчики обнуляются.
 */
public class LogTimer extends TimerTask {

    private static final Logger LOGGER = getLogger(LogTimer.class);

    public void run()
    {
        LOGGER.info("Request count for getAmount = " + AmountServlet.readStatistic);
        AmountServlet.readStatistic = 0;
        LOGGER.info("Request count for addAmount = " + AmountServlet.writeStatistic);
        AmountServlet.writeStatistic = 0;
    }

}
