package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.system.entity.Group;
import com.qingfeng.utils.PageData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName GroupMapper
 * @author Administrator
 * @version 1.0.0
 * @Description GroupMapper
 * @createTime 2022/1/19 0019 23:52
 */
public interface GroupMapper extends BaseMapper<Group> {

    //查询数据分页列表
    IPage<Group> findListPage(Page page, @Param("obj") Group group);

    //查询数据列表
    List<Group> findList(Group group);

    //查询组用户
    List<PageData> findGroupUser(PageData pd);

}