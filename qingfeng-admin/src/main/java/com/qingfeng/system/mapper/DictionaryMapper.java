package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.system.entity.Dictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName DictionaryMapper
 * @author Administrator
 * @version 1.0.0
 * @Description DictionaryMapper
 * @createTime 2022/1/19 0019 22:54
 */
public interface DictionaryMapper extends BaseMapper<Dictionary> {

    //查询数据分页列表
    IPage<Dictionary> findListPage(Page page, @Param("obj") Dictionary dictionary);

    //查询数据列表
    List<Dictionary> findList(Dictionary dictionary);

}