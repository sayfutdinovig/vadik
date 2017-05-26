package com.splat.services.impl;

import com.splat.database.DBManager;
import com.splat.services.AccountService;

/*
  Сервис, осуществляющий работу с балансом
 */
public class AccountServiceImpl implements AccountService
{
    private DBManager dbManager;
    
    public AccountServiceImpl()
    {
        dbManager = new DBManager();
    }


    @Override
    public Long getAmount(Integer id) throws Exception
    {
        return dbManager.getAmount(id);
    }


    @Override
    public void addAmount(Integer id, Long value) throws Exception
    {
        dbManager.addAmount(id, value);
    }

}
