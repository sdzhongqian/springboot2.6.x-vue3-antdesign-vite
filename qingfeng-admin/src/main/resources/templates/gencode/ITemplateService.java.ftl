package ${tablePd.pack_path}.${tablePd.mod_name}.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import ${tablePd.pack_path}.${tablePd.mod_name}.entity.${tablePd.bus_name?cap_first};

import java.util.List;

/**
 * @ProjectName I${tablePd.bus_name?cap_first}Service
 * @author Administrator
 * @version 1.0.0
 * @Description I${tablePd.bus_name?cap_first}Service接口
 * @createTime 2022/1/19 0019 22:55
 */
public interface I${tablePd.bus_name?cap_first}Service extends IService<${tablePd.bus_name?cap_first}> {

    //查询数据分页列表
    IPage<${tablePd.bus_name?cap_first}> findListPage(${tablePd.bus_name?cap_first} ${tablePd.bus_name}, QueryRequest request);

    //查询数据列表
    List<${tablePd.bus_name?cap_first}> findList(${tablePd.bus_name?cap_first} ${tablePd.bus_name});

}