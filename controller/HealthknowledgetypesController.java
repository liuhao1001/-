package myproject.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import myproject.common.page.PageResult;
import myproject.common.utils.Result;
import myproject.convert.HealthknowledgetypesConvert;
import myproject.entity.HealthknowledgetypesEntity;
import myproject.service.HealthknowledgetypesService;
import myproject.query.HealthknowledgetypesQuery;
import myproject.vo.HealthknowledgetypesVO;
import org.springframework.web.bind.annotation.*;
import myproject.common.request.RequestSingleParam;
import myproject.common.sysLog.SysLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
/**
* 养生知识类型
*/
@RestController
@RequestMapping("healthknowledgetypes")
@Tag(name="养生知识类型")
@AllArgsConstructor
public class HealthknowledgetypesController {
private final HealthknowledgetypesService healthknowledgetypesService;
    /**
     * 查看养生知识类型分页
     */
    @PostMapping("page")
    @Operation(summary = "分页")
    @SysLog(title = "查看养生知识类型分页")
    public Result<PageResult<HealthknowledgetypesVO>> page(@RequestBody @Valid HealthknowledgetypesQuery query){
        PageResult<HealthknowledgetypesVO> page = healthknowledgetypesService.page(query);
        return Result.ok(page);
    }
    /**
     * 查看养生知识类型列表
     */
    @PostMapping("list")
    @Operation(summary = "列表")
    @SaIgnore
    @SysLog(title = "查看养生知识类型列表")
    public Result<List<HealthknowledgetypesVO>> list(@RequestBody @Valid HealthknowledgetypesQuery query){
        List<HealthknowledgetypesVO> list = healthknowledgetypesService.queryList(query);
        return Result.ok(list);
    }
    /**
     * 查看养生知识类型信息
     */
    @PostMapping("/info")
    @Operation(summary = "信息")
    @SysLog(title = "查看养生知识类型信息")
    public Result<HealthknowledgetypesVO> get(@RequestSingleParam(value = "id") Long id){
        HealthknowledgetypesEntity entity = healthknowledgetypesService.getById(id);
        return Result.ok(HealthknowledgetypesConvert.INSTANCE.convert(entity));
    }
    /**
     * 保存养生知识类型信息
     */
    @PostMapping("save")
    @Operation(summary = "保存")
    @SysLog(title = "保存养生知识类型信息")
    public Result<String> save(@RequestBody HealthknowledgetypesVO vo){
        healthknowledgetypesService.save(vo);
        return Result.ok();
    }
    /**
     * 修改养生知识类型信息
     */
    @PostMapping("update")
    @Operation(summary = "修改")
    @SysLog(title = "修改养生知识类型信息")
    public Result<String> update(@RequestBody @Valid HealthknowledgetypesVO vo){
        healthknowledgetypesService.update(vo);
        return Result.ok();
    }
    /**
     * 删除养生知识类型信息
     */
    @PostMapping("delete")
    @Operation(summary = "删除")
    @SysLog(title = "删除养生知识类型信息")
    public Result<String> delete(@RequestBody List<Long> idList){
            healthknowledgetypesService.delete(idList);
            return Result.ok();
    }
    /**
     * 导出养生知识类型列表
     */
    @PostMapping("export")
    @Operation(summary = "导出")
    @SysLog(title = "导出养生知识类型列表")
    public void export(@RequestBody @Valid HealthknowledgetypesQuery query) {
        healthknowledgetypesService.export(query);
    }
    /**
     * 导入养生知识类型列表
     */
    @PostMapping("import")
    @Operation(summary = "导入")
    public Result<String> importhealthknowledgetypes(@RequestSingleParam(value = "file") String file) {
        if (file.isEmpty()) {
            return Result.error("请选择需要上传的文件");
        }
        file = file.replace("api/", "");
        File importFile = new File(file);
        healthknowledgetypesService.importhealthknowledgetypes(importFile);
        return Result.ok();
    }
    /**
    * （按值统计）
    */
    @SaIgnore
    @RequestMapping("/value/{xColumnName}/{yColumnName}")
    public Result value(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName, HttpServletRequest request) {
    Map<String, Object> params = new HashMap<>();
    params.put("xColumn", xColumnName);
    params.put("yColumn", yColumnName);
    LambdaQueryWrapper<HealthknowledgetypesEntity> wrapper = Wrappers.lambdaQuery();
    List<Map<String, Object>> result = healthknowledgetypesService.selectValue(params, wrapper);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    for (Map<String, Object> m : result) {
        for (String k : m.keySet()) {
            if (m.get(k) instanceof Date) {
                m.put(k, sdf.format((Date) m.get(k)));
            }
        }
    }
    return Result.ok(result);
    }

    /**
    * （按值统计）时间统计类型
    */
    @SaIgnore
    @RequestMapping("/value/{xColumnName}/{yColumnName}/{timeStatType}")
    public Result valueDay(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName, @PathVariable("timeStatType") String timeStatType, HttpServletRequest request) {
    Map<String, Object> params = new HashMap<>();
    params.put("xColumn", xColumnName);
    params.put("yColumn", yColumnName);
    params.put("timeStatType", timeStatType);
    LambdaQueryWrapper<HealthknowledgetypesEntity> wrapper = Wrappers.lambdaQuery();
        List<Map<String, Object>> result = healthknowledgetypesService.selectTimeStatValue(params, wrapper);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String, Object> m : result) {
            for (String k : m.keySet()) {
                if (m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date) m.get(k)));
                }
            }
        }
    return Result.ok(result);
    }

    /**
    * 分组统计
    */
    @SaIgnore
    @RequestMapping("/group/{columnName}")
    public Result group(@PathVariable("columnName") String columnName, HttpServletRequest request) {
    Map<String, Object> params = new HashMap<>();
    params.put("column", columnName);
    LambdaQueryWrapper<HealthknowledgetypesEntity> wrapper = Wrappers.lambdaQuery();
        //		wrapper.orderByDesc(HealthknowledgetypesEntity::getCount);
        List<Map<String, Object>> result = healthknowledgetypesService.selectGroup(params, wrapper);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String, Object> m : result) {
            for (String k : m.keySet()) {
                if (m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date) m.get(k)));
                }
            }
        }
    return Result.ok(result);
    }
}