package vn.edu.hcmute.khanggearver2.service;
import java.math.BigDecimal; import java.util.Map; import vn.edu.hcmute.khanggearver2.domain.OrderStatus;
public record StatisticsSummary(long products,long activeProducts,long lowStockProducts,long categories,long users,long orders,BigDecimal completedRevenue,Map<OrderStatus,Long> ordersByStatus) {}
