package com.PT.service.impl;

import com.PT.dao.SetAccRecInfoMapper;
import com.PT.dao.SettleAccRecordMapper;
import com.PT.entity.SettleAccRecord;
import com.PT.entity.SettleAccRecordExample;
import com.PT.service.SetAccRecService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * created by yxhuang
 * 结算管理具体业务逻辑实现
 */
@Service
public class SetAccRecServiceImpl implements SetAccRecService{

    @Autowired
    SetAccRecInfoMapper setAccRecInfoMapper;
    @Autowired
    SettleAccRecordMapper settleAccRecordMapper;
    @Override
    public Map<String, Object> listSetAccRec(int page, int ipp, int userId, Map factors) {
        Map<String, Object> map = new HashMap<>();
        factors.put("userId", userId);
        if(factors.get("state") != null) {
            factors.put("state", factors.get("state"));
        }
        PageHelper.startPage(page, ipp);
        List list = setAccRecInfoMapper.selectByFactors(factors);
        int maxPage = (setAccRecInfoMapper.countByFactors(factors)-1)/ipp+1;
        map.put("maxPage", maxPage);
        map.put("records", list);
        return map;
    }

    @Override
    public Boolean deleteSetAccRec(List<String> setAccIds) {
        SettleAccRecordExample example = new SettleAccRecordExample();
        for(int i = 0; i < setAccIds.size(); i++)
            System.out.println(setAccIds.get(i));
        example.createCriteria().andStatusEqualTo(2).andSetAccIdIn(setAccIds);
        try {
            settleAccRecordMapper.deleteByExample(example);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean updateSetAccState(List<String> setAccIds, int state) {
        SettleAccRecordExample example = new SettleAccRecordExample();
        example.createCriteria().andStatusEqualTo(1).andSetAccIdIn(setAccIds);
        SettleAccRecord record = new SettleAccRecord();
        record.setStatus(state);
        try {
            if(state == 2) {
                record.setTradedAt(new Date());
            }
            settleAccRecordMapper.updateByExampleSelective(record, example);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}