package myproject.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

import myproject.common.page.PageResult;
import myproject.common.utils.PwdUtil;
import myproject.common.utils.Result;
import myproject.convert.AdminConvert;
import myproject.entity.AdminEntity;
import myproject.service.AdminService;
import myproject.query.AdminQuery;
import myproject.vo.AdminVO;
import myproject.vo.SysAccountLoginVO;
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
import myproject.common.utils.TokenEntity;
import myproject.constants.Constants;
import cn.hutool.core.util.StrUtil;
/**
* 后台管理用户表
*
*/
@RestController
@RequestMapping("admin")
@Tag(name="后台管理用户表")
@AllArgsConstructor
public class AdminController {
private final AdminService adminService;
     private static final String StpPre = "admin";
    /**
    	 * 账号密码登录
    	 */
    	@SaIgnore
    	@PostMapping(value = "/login")
    	@SysLog(title = "后台管理用户表登录", content = "系统日志")
    	public Result<TokenEntity> login(@RequestBody SysAccountLoginVO sysAccountLoginVO) {
    		AdminEntity user = adminService.getOne(new QueryWrapper<AdminEntity>().eq("username", sysAccountLoginVO.getUsername()));
    		if (user == null || !user.getPassword().equals(PwdUtil.encrypt(sysAccountLoginVO.getPassword()))) {
    			return Result.error("账号或密码不正确");
    		}
    		StpUtil.login(StpPre + user.getId());
    		TokenEntity tokenEntity = new TokenEntity();
    		tokenEntity.setUsername(user.getUsername());
    		tokenEntity.setUserid(user.getId());
    		tokenEntity.setRole(user.getRole());
    		tokenEntity.setTablename(StpPre);
    		tokenEntity.setAddtime(user.getAddtime());
    		tokenEntity.setAvatarurl(user.getAvatarurl());
    		tokenEntity.setName(user.getName());
    		tokenEntity.setToken(StpUtil.getTokenValue());
    		StpUtil.getSession().set(Constants.CURRENT_USER, tokenEntity);
    		return Result.ok(tokenEntity);
    	}

    	/**
    	 * 注册
    	 */
    	@SaIgnore
    	@PostMapping(value = "/register")
    	@SysLog(title = "后台管理用户表注册")
    	public Result<Boolean> register(@RequestBody AdminEntity user) {
    		if (adminService.getOne(new QueryWrapper<AdminEntity>().eq("username", user.getUsername())) != null) {
    			return Result.error("用户已存在");
    		}
    		user.setPassword(PwdUtil.encrypt(user.getPassword()));
    		adminService.save(user);
    		return Result.ok(true);
    	}
    @PostMapping("page")
    @Operation(summary = "分页")
    @SysLog(title = "查看后台管理用户表分页")
    public Result<PageResult<AdminVO>> page(@RequestBody @Valid AdminQuery query){
        PageResult<AdminVO> page = adminService.page(query);
        return Result.ok(page);
    }
    @PostMapping("list")
    @Operation(summary = "列表")
    @SaIgnore
    @SysLog(title = "查看后台管理用户表列表")
    public Result<List<AdminVO>> list(@RequestBody @Valid AdminQuery query){
        List<AdminVO> list = adminService.queryList(query);
        return Result.ok(list);
    }
    @PostMapping("/info")
    @Operation(summary = "信息")
    @SysLog(title = "查看后台管理用户表信息")
    public Result<AdminVO> get(@RequestSingleParam(value = "id") Long id){
        AdminEntity entity = adminService.getById(id);
        return Result.ok(AdminConvert.INSTANCE.convert(entity));
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @SysLog(title = "保存后台管理用户表信息")
    public Result<String> save(@RequestBody AdminVO vo){
                    if (StrUtil.isNotBlank(vo.getPassword())) {
                        vo.setPassword(PwdUtil.encrypt(vo.getPassword()));
                    }
        adminService.save(vo);
        return Result.ok();
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @SysLog(title = "修改后台管理用户表信息")
    public Result<String> update(@RequestBody @Valid AdminVO vo){
                    if (StrUtil.isNotBlank(vo.getPassword())) {
                        vo.setPassword(PwdUtil.encrypt(vo.getPassword()));
                    }
        adminService.update(vo);
        return Result.ok();
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @SysLog(title = "删除后台管理用户表信息")
    public Result<String> delete(@RequestBody List<Long> idList){
            adminService.delete(idList);
            return Result.ok();
    }

    @PostMapping("export")
    @Operation(summary = "导出")
    @SysLog(title = "导出后台管理用户表列表")
    public void export(@RequestBody @Valid AdminQuery query) {
        adminService.export(query);
    }
    @PostMapping("import")
    @Operation(summary = "导入")
    public Result<String> importadmin(@RequestSingleParam(value = "file") String file) {
        if (file.isEmpty()) {
            return Result.error("请选择需要上传的文件");
        }
        file = file.replace("api/", "");
        File importFile = new File(file);
        adminService.importadmin(importFile);
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
    LambdaQueryWrapper<AdminEntity> wrapper = Wrappers.lambdaQuery();
    List<Map<String, Object>> result = adminService.selectValue(params, wrapper);
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
    LambdaQueryWrapper<AdminEntity> wrapper = Wrappers.lambdaQuery();
        List<Map<String, Object>> result = adminService.selectTimeStatValue(params, wrapper);
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
    LambdaQueryWrapper<AdminEntity> wrapper = Wrappers.lambdaQuery();
        //		wrapper.orderByDesc(AdminEntity::getCount);
        List<Map<String, Object>> result = adminService.selectGroup(params, wrapper);
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