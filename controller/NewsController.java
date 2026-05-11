package myproject.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import myproject.common.page.PageResult;
import myproject.common.request.RequestSingleParam;
import myproject.common.sysLog.SysLog;
import myproject.common.utils.Result;
import myproject.convert.NewsConvert;
import myproject.entity.NewsEntity;
import myproject.query.NewsQuery;
import myproject.service.NewsService;
import myproject.vo.NewsVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 公告信息
 */
@RestController
@RequestMapping("news")
@Tag(name = "公告信息")
@AllArgsConstructor
public class NewsController {
	private final NewsService newsService;

	@PostMapping("page")
	@Operation(summary = "分页")
	@SysLog(title = "查看公告分页信息")
	public Result<PageResult<NewsVO>> page(@RequestBody @Valid NewsQuery query) {
		PageResult<NewsVO> page = newsService.page(query);
		return Result.ok(page);
	}

	@PostMapping("list")
	@Operation(summary = "列表")
	@SaIgnore
	@SysLog(title = "查看公告列表")
	public Result<List<NewsVO>> list(@RequestBody @Valid NewsQuery query) {
		List<NewsVO> list = newsService.queryList(query);
		return Result.ok(list);
	}

	@PostMapping("/info")
	@Operation(summary = "信息")
	@SaIgnore
	@SysLog(title = "查看公告信息")
	public Result<NewsVO> get(@RequestSingleParam(value = "id") Long id) {
		NewsEntity entity = newsService.getById(id);
		return Result.ok(NewsConvert.INSTANCE.convert(entity));
	}

	@PostMapping("save")
	@Operation(summary = "保存")
	@SysLog(title = "保存公告信息")
	public Result<String> save(@RequestBody NewsVO vo) {
		newsService.save(vo);
		return Result.ok();
	}

	@PostMapping("update")
	@Operation(summary = "修改")
	@SysLog(title = "修改公告信息")
	public Result<String> update(@RequestBody @Valid NewsVO vo) {
		newsService.update(vo);
		return Result.ok();
	}

	@PostMapping("delete")
	@Operation(summary = "删除")
	@SysLog(title = "删除公告信息")
	public Result<String> delete(@RequestBody List<Long> idList) {
		newsService.delete(idList);
		return Result.ok();
	}

	@PostMapping("export")
	@Operation(summary = "导出")
	@SysLog(title = "导出公告信息")
	public void export(@RequestBody @Valid NewsQuery query) {
		newsService.export(query);
	}
}