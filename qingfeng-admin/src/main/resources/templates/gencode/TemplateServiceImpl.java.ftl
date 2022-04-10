package ${tablePd.pack_path}.${tablePd.mod_name}.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.base.entity.QueryRequest;
import ${tablePd.pack_path}.${tablePd.mod_name}.entity.${tablePd.bus_name?cap_first};
import ${tablePd.pack_path}.${tablePd.mod_name}.mapper.${tablePd.bus_name?cap_first}Mapper;
import ${tablePd.pack_path}.${tablePd.mod_name}.service.I${tablePd.bus_name?cap_first}Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName ${tablePd.bus_name?cap_first}ServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description ${tablePd.bus_name?cap_first}ServiceImpl接口
 * @createTime 2022/1/19 0019 22:55
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ${tablePd.bus_name?cap_first}ServiceImpl extends ServiceImpl<${tablePd.bus_name?cap_first}Mapper, ${tablePd.bus_name?cap_first}> implements I${tablePd.bus_name?cap_first}Service {


    /**
     * @ProjectName ${tablePd.bus_name?cap_first}ServiceImpl
     * @author Administrator
     * @version 1.0.0
     * @Description 查询数据分页列表
     * @createTime 2022/1/19 0019 22:55
     */
    public IPage<${tablePd.bus_name?cap_first}> findListPage(${tablePd.bus_name?cap_first} ${tablePd.bus_name}, QueryRequest request){
        Page<${tablePd.bus_name?cap_first}> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.findListPage(page, ${tablePd.bus_name});
    }

    /**
     * @ProjectName ${tablePd.bus_name?cap_first}ServiceImpl
     * @author Administrator
     * @version 1.0.0
     * @Description 查询数据列表
     * @createTime 2022/1/19 0019 22:55
     */
    public List<${tablePd.bus_name?cap_first}> findList(${tablePd.bus_name?cap_first} ${tablePd.bus_name}){
        return this.baseMapper.findList(${tablePd.bus_name});
    }


}