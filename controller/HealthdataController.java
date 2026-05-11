package myproject.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import myproject.common.page.PageResult;
import myproject.common.utils.Result;
import myproject.convert.HealthdataConvert;
import myproject.entity.HealthdataEntity;
import myproject.service.HealthdataService;
import myproject.query.HealthdataQuery;
import myproject.vo.HealthdataVO;
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
* 养生数据
*/
@RestController
@RequestMapping("healthdata")
@Tag(name="养生数据")
@AllArgsConstructor
public class HealthdataController {
private final HealthdataService healthdataService;
    /**
     * 查看养生数据分页
     */
    @PostMapping("page")
    @Operation(summary = "分页")
    @SysLog(title = "查看养生数据分页")
    public Result<PageResult<HealthdataVO>> page(@RequestBody @Valid HealthdataQuery query){
        PageResult<HealthdataVO> page = healthdataService.page(query);
        return Result.ok(page);
    }
    /**
     * 查看养生数据列表
     */
    @PostMapping("list")
    @Operation(summary = "列表")
    @SaIgnore
    @SysLog(title = "查看养生数据列表")
    public Result<List<HealthdataVO>> list(@RequestBody @Valid HealthdataQuery query){
        List<HealthdataVO> list = healthdataService.queryList(query);
        return Result.ok(list);
    }
    /**
     * 查看养生数据信息
     */
    @PostMapping("/info")
    @Operation(summary = "信息")
    @SysLog(title = "查看养生数据信息")
    public Result<HealthdataVO> get(@RequestSingleParam(value = "id") Long id){
        HealthdataEntity entity = healthdataService.getById(id);
        return Result.ok(HealthdataConvert.INSTANCE.convert(entity));
    }
    /**
     * 保存养生数据信息
     */
    @PostMapping("save")
    @Operation(summary = "保存")
    @SysLog(title = "保存养生数据信息")
    public Result<String> save(@RequestBody HealthdataVO vo){
        healthdataService.save(vo);
        return Result.ok();
    }
    /**
     * 修改养生数据信息
     */
    @PostMapping("update")
    @Operation(summary = "修改")
    @SysLog(title = "修改养生数据信息")
    public Result<String> update(@RequestBody @Valid HealthdataVO vo){
        healthdataService.update(vo);
        return Result.ok();
    }
    /**
     * 删除养生数据信息
     */
    @PostMapping("delete")
    @Operation(summary = "删除")
    @SysLog(title = "删除养生数据信息")
    public Result<String> delete(@RequestBody List<Long> idList){
            healthdataService.delete(idList);
            return Result.ok();
    }
    /**
     * 导出养生数据列表
     */
    @PostMapping("export")
    @Operation(summary = "导出")
    @SysLog(title = "导出养生数据列表")
    public void export(@RequestBody @Valid HealthdataQuery query) {
        healthdataService.export(query);
    }
    /**
     * 导入养生数据列表
     */
    @PostMapping("import")
    @Operation(summary = "导入")
    public Result<String> importhealthdata(@RequestSingleParam(value = "file") String file) {
        if (file.isEmpty()) {
            return Result.error("请选择需要上传的文件");
        }
        file = file.replace("api/", "");
        File importFile = new File(file);
        healthdataService.importhealthdata(importFile);
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
    LambdaQueryWrapper<HealthdataEntity> wrapper = Wrappers.lambdaQuery();
    List<Map<String, Object>> result = healthdataService.selectValue(params, wrapper);
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
    LambdaQueryWrapper<HealthdataEntity> wrapper = Wrappers.lambdaQuery();
        List<Map<String, Object>> result = healthdataService.selectTimeStatValue(params, wrapper);
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
    LambdaQueryWrapper<HealthdataEntity> wrapper = Wrappers.lambdaQuery();
        //		wrapper.orderByDesc(HealthdataEntity::getCount);
        List<Map<String, Object>> result = healthdataService.selectGroup(params, wrapper);
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