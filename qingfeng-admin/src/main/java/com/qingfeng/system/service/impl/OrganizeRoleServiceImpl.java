package com.qingfeng.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.system.entity.OrganizeRole;
import com.qingfeng.system.mapper.OrganizeRoleMapper;
import com.qingfeng.system.service.IOrganizeRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ProjectName OrganizeRoleServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description OrganizeRoleServiceImpl实现类
 * @createTime 2022/1/20 0020 0:18
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class OrganizeRoleServiceImpl extends ServiceImpl<OrganizeRoleMapper, OrganizeRole> implements IOrganizeRoleService {


}