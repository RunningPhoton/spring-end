package com.PT.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    public Map<String,Object> listKnowledge(int userId, String type, int page, int ipp, String queryCondition) throws Exception
    {

        return null;
    }

    public void deleteKnowledge(int userId, List<String> knowledgeIds) throws Exception{


    }

    public void addKnowledge(int userId,String type) throws Exception
    {

    }
}
