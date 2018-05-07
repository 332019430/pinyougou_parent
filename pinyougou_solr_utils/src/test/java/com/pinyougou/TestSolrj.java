package com.pinyougou;

import com.pinyougou.pojo.TbItem;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.jupiter.api.Test;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import java.io.IOException;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou
 * @date 2018/5/6/0006
 */
public class TestSolrj {
    //add
    @Test
    public  void add() throws IOException, SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://192.168.25.133:8080/solr");
        SolrInputDocument document = new SolrInputDocument();

        document.addField("id","test001");
        document.addField("item_title","测试的solrItem");

        solrServer.add(document);
        solrServer.commit();
    }

    @Test
    public  void query() throws IOException, SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://192.168.25.133:8080/solr");
        SolrQuery solrQuery = new SolrQuery("*:*");

        QueryResponse response = solrServer.query(solrQuery);
        SolrDocumentList results = response.getResults();

        long numFound = results.getNumFound();
        System.out.println(numFound);

        for (SolrDocument result : results) {
            System.out.println(result.get("id")+":"+result.get("item_title"));
        }
    }

    @Test
    public  void addz() throws IOException, SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://192.168.25.133:8080/solr");
        TbItem tbItem = new TbItem();
        tbItem.setId(new Long(2));
        tbItem.setTitle("测试注解");
        solrServer.addBean(tbItem);
        solrServer.commit();
    }

    @Test
    public  void delete() throws IOException, SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://192.168.25.133:8080/solr");

        solrServer.deleteByQuery("*:*");
        solrServer.commit();
    }
}
