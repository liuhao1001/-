package myproject.common.config;

import lombok.Data;

@Data
public class AliPay {
	// 我们自己生成的订单编号
	private String traceNo;
	// 订单的总金额
	private double totalAmount;
	// 支付的名称
	private String subject;
	private String alipayTraceNo;
}
