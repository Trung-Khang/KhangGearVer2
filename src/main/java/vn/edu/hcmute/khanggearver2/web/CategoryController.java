package vn.edu.hcmute.khanggearver2.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.hcmute.khanggearver2.domain.Category;
import vn.edu.hcmute.khanggearver2.exception.DuplicateResourceException;
import vn.edu.hcmute.khanggearver2.exception.ResourceNotFoundException;
import vn.edu.hcmute.khanggearver2.service.CategoryService;

@Controller
@Profile("!foundation")
@RequestMapping("/admin/category")
public class CategoryController {
    private static final long MAX_ICON_BYTES = 2 * 1024 * 1024;
    private final CategoryService categories;
    private final Path uploadRoot;
    public CategoryController(CategoryService categories, @Value("${app.upload-dir:uploads/categories}") String uploadDir) {
        this.categories = categories; this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }
    @GetMapping({"", "/list"})
    String list(@RequestParam(defaultValue = "") String keyword, Model model) {
        model.addAttribute("categories", categories.search(keyword, org.springframework.data.domain.PageRequest.of(0, 100, org.springframework.data.domain.Sort.by("name").ascending())));
        model.addAttribute("keyword", keyword == null ? "" : keyword.trim()); return "admin/category/list";
    }
    @GetMapping("/add") String add(Model model) { if (!model.containsAttribute("category")) model.addAttribute("category", new Category()); return "admin/category/add"; }
    @PostMapping("/add")
    String add(@RequestParam(required = false) String name, @RequestParam(required = false) String description, @RequestParam(required = false) MultipartFile icon, Model model, RedirectAttributes flash) {
        Category category = category(name, description); String error = validateIcon(icon); if (error != null) return addError(model, category, error);
        if (name == null || name.trim().isEmpty()) return addError(model, category, "Tên danh mục là bắt buộc.");
        String storedIcon = null;
        try { storedIcon = store(icon); category.setIcon(storedIcon); categories.create(category); flash.addFlashAttribute("message", "Thêm danh mục thành công."); }
        catch (RuntimeException | IOException exception) { deleteStoredIcon(storedIcon); model.addAttribute("error", message(exception, "Không thể thêm danh mục.")); model.addAttribute("category", category); return "admin/category/add"; }
        return "redirect:/admin/category/list";
    }
    @GetMapping("/edit") String edit(@RequestParam Long id, Model model) { model.addAttribute("category", categories.getById(id)); return "admin/category/edit"; }
    @PostMapping("/edit")
    String edit(@RequestParam Long id, @RequestParam(required = false) String name, @RequestParam(required = false) String description, @RequestParam(required = false) MultipartFile icon, Model model, RedirectAttributes flash) {
        Category input = category(name, description); String error = validateIcon(icon); if (error != null) return editError(model, id, input, error);
        if (name == null || name.trim().isEmpty()) return editError(model, id, input, "Tên danh mục là bắt buộc.");
        String storedIcon = null;
        try { Category current = categories.getById(id); storedIcon = icon != null && !icon.isEmpty() ? store(icon) : null; input.setIcon(storedIcon != null ? storedIcon : current.getIcon()); categories.update(id, input); flash.addFlashAttribute("message", "Cập nhật danh mục thành công."); }
        catch (RuntimeException | IOException exception) { deleteStoredIcon(storedIcon); model.addAttribute("error", message(exception, "Không thể cập nhật danh mục.")); model.addAttribute("category", input); model.addAttribute("id", id); return "admin/category/edit"; }
        return "redirect:/admin/category/list";
    }
    @PostMapping("/delete")
    String delete(@RequestParam Long id, RedirectAttributes flash) {
        try { categories.delete(id); flash.addFlashAttribute("message", "Xóa danh mục thành công."); }
        catch (DataIntegrityViolationException exception) { flash.addFlashAttribute("error", "Không thể xóa danh mục vì đang có sản phẩm sử dụng."); }
        catch (ResourceNotFoundException exception) { flash.addFlashAttribute("error", "Không tìm thấy danh mục."); }
        catch (RuntimeException exception) { flash.addFlashAttribute("error", "Không thể xóa danh mục."); }
        return "redirect:/admin/category/list";
    }
    private Category category(String name, String description) { Category c = new Category(); c.setName(name); c.setDescription(description); return c; }
    private String addError(Model model, Category c, String error) { model.addAttribute("category", c); model.addAttribute("error", error); return "admin/category/add"; }
    private String editError(Model model, Long id, Category c, String error) { model.addAttribute("id", id); model.addAttribute("category", c); model.addAttribute("error", error); return "admin/category/edit"; }
    private String validateIcon(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        String type = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        boolean extension = name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".webp");
        boolean mime = type.equals("image/png") || type.equals("image/jpeg") || type.equals("image/webp");
        if (!extension || !mime) return "Icon chỉ chấp nhận PNG, JPG/JPEG hoặc WebP.";
        return file.getSize() > MAX_ICON_BYTES ? "Icon không được vượt quá 2 MB." : null;
    }
    private String store(MultipartFile file) throws IOException {
        Files.createDirectories(uploadRoot); String original = file.getOriginalFilename(); String extension = original.substring(original.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        String filename = java.util.UUID.randomUUID() + extension; Path target = uploadRoot.resolve(filename).normalize(); if (!target.getParent().equals(uploadRoot)) throw new IOException("Invalid upload path"); Files.write(target, file.getBytes()); return filename;
    }
    private void deleteStoredIcon(String filename) {
        if (filename == null || filename.isBlank()) return;
        try { Files.deleteIfExists(uploadRoot.resolve(filename).normalize()); } catch (IOException ignored) { }
    }
    private String message(Exception exception, String fallback) { return exception instanceof DuplicateResourceException ? "Tên danh mục đã tồn tại." : fallback; }
}
