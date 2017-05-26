package com.splat.servlets;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;

import com.splat.logger.LogTimer;
import com.splat.services.impl.AccountServiceImpl;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.xml.XmlConfiguration;

import java.util.TimerTask;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManager;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * Сервлет, обрабатывающий запросы на получение и изменение баланса.
 */
@WebServlet(name = "AmountServlet", urlPatterns = "/server")
public class AmountServlet extends HttpServlet
{
    //параметры, указываемые в запросах
    private final static String ID_PARAM = "id";
    private final static String VALUE_PARAM = "value";

    //Интервал для таймера
    private final static int MINUTE = 60*1000;

    //Кэш и таймер
    private Cache<Integer, Long> cache;
    private Timer timer;

    //Статистические счетчики. Подсчитывают количество GET,POST - запросов
    public static volatile int readStatistic = 0;
    public static volatile int writeStatistic = 0;
    public static int totalReadStatistic = 0;
    public static int totalWriteStatistic = 0;

    // Инициализируем кэш и запускаем таймер с перидочиностью равной MINUTE.
    @Override
    public void init()
    {
        Configuration xmlConfig = new XmlConfiguration(AmountServlet.class.getResource("/ehcache.xml"));
        CacheManager cacheManager = newCacheManager(xmlConfig);
        cacheManager.init();
        cache = cacheManager.getCache("basicCache", Integer.class, Long.class);

        timer = new Timer();
        TimerTask task = new LogTimer();
        timer.schedule(task,0,MINUTE);
    }

    // При завершении работы сервиса очищаем кэш и останавливаем таймер.
    @Override
    public void destroy()
    {
        cache.clear();
        timer.cancel();
    }

    // Обрабатываем GET - запрос
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try
        {
            boolean containId = request.getParameterMap().containsKey(ID_PARAM);
            //  Если в запросе участвует параметр id и его значение является числом, то обрабатываем его.
            if (containId)
            {
                Integer id;
                try {
                    id = Integer.parseInt(request.getParameter(ID_PARAM));
                } catch (NumberFormatException nfe) {
                    out.println("Parameter id must be number!");
                    out.close();
                    return;
                }

                // Если в кэше нет значения для id, получаем его с сервера.
                if (cache.get(id) == null)
                {
                    cache.put(id, processGetAmountRequest(id));
                }
                out.println(cache.get(id));
                // В случае успешных запросов - ведем их подсчет.
                readStatistic++;
                totalReadStatistic++;
            }
            else
            {
                out.println("Incorrect request.");
            }
        }
        catch (Exception e)
        {
            out.println(e.getMessage());
        }
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
            boolean containId = request.getParameterMap().containsKey(ID_PARAM);
            boolean containValue = request.getParameterMap().containsKey(VALUE_PARAM);
            // Если в запросе участвуют параметры id и value, и их значение является числами, то обрабатываем его.
            if (containId && containValue)
            {
                Integer id;
                Long value;
                try {
                    id = Integer.parseInt(request.getParameter(ID_PARAM));
                    value = Long.parseLong(request.getParameter(VALUE_PARAM));
                } catch (NumberFormatException nfe) {
                    out.println("Parameter id and value must be number!");
                    out.close();
                    return;
                }
                // Производим изенения в БД. Добавляем значения в кэш.
                processAddAmountRequest(id, value);
                cache.put(id,value);
                out.println("Value for id = " + id + " updated.");
                writeStatistic++;
                totalWriteStatistic++;
            } else
                {
                    out.println("Incorrect request.");
                }
        }
        catch (Exception e)
        {
            out.println(e.getMessage());
        }
        out.close();
    }

    /**
     * Обработка запроса получения текущего баланса
     * @param id идентификатор баланса
     * @return значение баланса
     * @throws Exception
     */
    private Long processGetAmountRequest(int id) throws Exception
    {
        AccountServiceImpl service = new AccountServiceImpl();
        return service.getAmount(id);
    }

    /**
     * Обработка запроса получения текущего баланса
     * @param id идентификатор баланса
     * @param value значение, добавляемое к балансу
     * @throws Exception
     */
    private void processAddAmountRequest(int id, Long value) throws Exception
    {
        AccountServiceImpl service = new AccountServiceImpl();
        service.addAmount(id,value);
    }

}
