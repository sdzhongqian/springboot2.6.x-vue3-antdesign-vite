package ${tablePd.pack_path}.${tablePd.mod_name}.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${tablePd.pack_path}.${tablePd.mod_name}.entity.${tablePd.bus_name?cap_first};
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName ${tablePd.bus_name?cap_first}Mapper
 * @author Administrator
 * @version 1.0.0
 * @Description ${tablePd.bus_name?cap_first}Mapper
 * @createTime 2022/1/19 0019 22:54
 */
public interface ${tablePd.bus_name?cap_first}Mapper extends BaseMapper<${tablePd.bus_name?cap_first}> {

    //查询数据分页列表
    IPage<${tablePd.bus_name?cap_first}> findListPage(Page page, @Param("obj") ${tablePd.bus_name?cap_first} ${tablePd.bus_name});

    //查询数据列表
    List<${tablePd.bus_name?cap_first}> findList(${tablePd.bus_name?cap_first} ${tablePd.bus_name});

}