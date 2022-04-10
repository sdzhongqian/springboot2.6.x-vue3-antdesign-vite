package ${tablePd.pack_path}.${tablePd.mod_name}.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName ${tablePd.bus_name?cap_first}
 * @author Administrator
 * @version 1.0.0
 * @Description 字典信息
 * @createTime 2022/1/19 0019 22:54
 */
@Data
@TableName("${tablePd.table_name}")
public class ${tablePd.bus_name?cap_first} implements Serializable {

    private static final long serialVersionUID = -4352868070794165001L;

<#list fieldList as obj>
<#if obj.field_name == 'id'>
    /**
    * 主键id
    */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;
</#if>
<#if obj.field_name != 'id'>
    /**
    * ${obj.field_comment}
    */
    @TableField("${obj.field_name}")
    private String ${obj.field_name};
</#if>
</#list>

<#if tablePd.temp_type == '1'>
    /**
    * 父级id
    */
    @TableField("parent_id")
    private String parent_id;

    @TableField(exist = false)
    private String parent_name;
</#if>

    @TableField(exist = false)
    private String start_time;

    @TableField(exist = false)
    private String end_time;

    @TableField(exist = false)
    private List<String> auth_organize_ids;

    @TableField(exist = false)
    private String auth_user;

    @TableField(exist = false)
    private String ids;

    @TableField(exist = false)
    private String child_num;
}