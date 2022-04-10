package com.qingfeng.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Dictionary;
import com.qingfeng.system.mapper.DictionaryMapper;
import com.qingfeng.system.service.IDictionaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName DictionaryServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description DictionaryServiceImpl接口
 * @createTime 2022/1/19 0019 22:55
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements IDictionaryService {

    /**
     * @ProjectName DictionaryServiceImpl
     * @author Administrator
     * @version 1.0.0
     * @Description 查询数据分页列表
     * @createTime 2022/1/19 0019 22:55
     */
    public IPage<Dictionary> findListPage(Dictionary dictionary, QueryRequest request){
        Page<Dictionary> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.findListPage(page, dictionary);
    }

    /**
     * @ProjectName DictionaryServiceImpl
     * @author Administrator
     * @version 1.0.0
     * @Description 查询数据列表
     * @createTime 2022/1/19 0019 22:55
     */
    public List<Dictionary> findList(Dictionary dictionary){
        return this.baseMapper.findList(dictionary);
    }


}