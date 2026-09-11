package vn.edu.hcmute.khanggearver2.service.impl;
import org.springframework.context.annotation.Profile; import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import vn.edu.hcmute.khanggearver2.domain.Category; import vn.edu.hcmute.khanggearver2.exception.DuplicateResourceException; import vn.edu.hcmute.khanggearver2.exception.ResourceNotFoundException; import vn.edu.hcmute.khanggearver2.repository.CategoryRepository; import vn.edu.hcmute.khanggearver2.service.CategoryService;
@Service @Profile("!foundation") public class CategoryServiceImpl implements CategoryService {
 private final CategoryRepository repository; public CategoryServiceImpl(CategoryRepository repository){this.repository=repository;}
 @Transactional public Category create(Category category){ normalize(category); rejectDuplicate(category.getName(), null); return repository.save(category); }
 @Transactional(readOnly=true) public Category getById(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Khong tim thay danh muc"));}
 @Transactional public Category update(Long id, Category input){Category current=getById(id); normalize(input); rejectDuplicate(input.getName(), id); current.setName(input.getName()); current.setDescription(input.getDescription()); current.setActive(input.getActive()); return repository.save(current);}
 @Transactional public void delete(Long id){repository.delete(getById(id));}
 @Transactional(readOnly=true) public Page<Category> search(String keyword, Pageable pageable){return repository.findByNameContainingIgnoreCase(keyword==null?"":keyword.trim(),pageable);}
 private void normalize(Category c){ c.setName(required(c.getName(),"Ten danh muc la bat buoc")); c.setDescription(blankToNull(c.getDescription())); if(c.getActive()==null)c.setActive(true); }
 private void rejectDuplicate(String name, Long currentId){repository.findByNameContainingIgnoreCase(name, Pageable.unpaged()).stream().filter(c->!c.getId().equals(currentId)).findAny().ifPresent(c->{throw new DuplicateResourceException("Ten danh muc da ton tai");});}
 private String required(String value,String message){String result=blankToNull(value);if(result==null)throw new IllegalArgumentException(message);return result;} private String blankToNull(String value){if(value==null)return null;String result=value.trim();return result.isEmpty()?null:result;}
}
