package myproject.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import myproject.common.page.PageResult;
import myproject.common.utils.Result;
import myproject.convert.StoreupConvert;
import myproject.entity.StoreupEntity;
import myproject.service.StoreupService;
import myproject.query.StoreupQuery;
import myproject.vo.StoreupVO;
import myproject.common.utils.GloabUtils;
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
* 收藏表
*
*/
@RestController
@RequestMapping("storeup")
@Tag(name="收藏表")
@AllArgsConstructor
public class StoreupController {
private final StoreupService storeupService;
    @PostMapping("page")
    @Operation(summary = "分页")
    @SysLog(title = "查看收藏表分页")
    public Result<PageResult<StoreupVO>> page(@RequestBody @Valid StoreupQuery query){
        PageResult<StoreupVO> page = storeupService.page(query);
        return Result.ok(page);
    }
    @PostMapping("list")
    @Operation(summary = "列表")
    @SaIgnore
    @SysLog(title = "查看收藏表列表")
    public Result<List<StoreupVO>> list(@RequestBody @Valid StoreupQuery query){
        List<StoreupVO> list = storeupService.queryList(query);
        return Result.ok(list);
    }
    @PostMapping("/info")
    @Operation(summary = "信息")
    @SysLog(title = "查看收藏表信息")
    public Result<StoreupVO> get(@RequestSingleParam(value = "id") Long id){
        StoreupEntity entity = storeupService.getById(id);
        return Result.ok(StoreupConvert.INSTANCE.convert(entity));
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @SysLog(title = "保存收藏表信息")
    public Result<String> save(@RequestBody StoreupVO vo){
        vo.setUserid(GloabUtils.getUserId());
        storeupService.save(vo);
        return Result.ok();
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @SysLog(title = "修改收藏表信息")
    public Result<String> update(@RequestBody @Valid StoreupVO vo){
        storeupService.update(vo);
        return Result.ok();
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @SysLog(title = "删除收藏表信息")
    public Result<String> delete(@RequestBody List<Long> idList){
            storeupService.delete(idList);
            return Result.ok();
    }

    @PostMapping("export")
    @Operation(summary = "导出")
    @SysLog(title = "导出收藏表列表")
    public void export(@RequestBody @Valid StoreupQuery query) {
        storeupService.export(query);
    }
    @PostMapping("import")
    @Operation(summary = "导入")
    public Result<String> importstoreup(@RequestSingleParam(value = "file") String file) {
        if (file.isEmpty()) {
            return Result.error("请选择需要上传的文件");
        }
        file = file.replace("api/", "");
        File importFile = new File(file);
        storeupService.importstoreup(importFile);
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
    LambdaQueryWrapper<StoreupEntity> wrapper = Wrappers.lambdaQuery();
    List<Map<String, Object>> result = storeupService.selectValue(params, wrapper);
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
    LambdaQueryWrapper<StoreupEntity> wrapper = Wrappers.lambdaQuery();
        List<Map<String, Object>> result = storeupService.selectTimeStatValue(params, wrapper);
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
    LambdaQueryWrapper<StoreupEntity> wrapper = Wrappers.lambdaQuery();
        //		wrapper.orderByDesc(StoreupEntity::getCount);
        List<Map<String, Object>> result = storeupService.selectGroup(params, wrapper);
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