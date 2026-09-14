package vn.edu.hcmute.khanggearver2.web;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.hcmute.khanggearver2.domain.OrderStatus;
import vn.edu.hcmute.khanggearver2.service.OrderService;

@Controller
@Profile("!foundation")
@RequestMapping("/admin/order")
public class OrderAdminController {
    private final OrderService orders;

    public OrderAdminController(OrderService orders) {
        this.orders = orders;
    }

    @GetMapping({"", "/list"})
    String list(@RequestParam(defaultValue = "") String keyword,
                @RequestParam(required = false) OrderStatus status,
                @RequestParam(defaultValue = "0") int page,
                Model model) {
        model.addAttribute("orders", orders.search(keyword, status,
                PageRequest.of(Math.max(0, page), 10, Sort.by("createdAt").descending())));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order/list";
    }

    @GetMapping("/detail")
    String detail(@RequestParam Long id, Model model) {
        model.addAttribute("order", orders.getById(id));
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order/detail";
    }

    @PostMapping("/update-status")
    String update(@RequestParam Long id, @RequestParam OrderStatus status, RedirectAttributes flash) {
        try {
            orders.updateStatus(id, status);
            flash.addFlashAttribute("message", "Cập nhật trạng thái đơn hàng thành công.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage() == null
                    ? "Không thể cập nhật đơn hàng." : ex.getMessage());
        }
        return "redirect:/admin/order/list";
    }
}
