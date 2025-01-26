package com.eachserver.blog;

import com.eachserver.application.feature.FeatureMapping;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BlogUpdateController {

    private final BlogRepository blogRepository;

    @FeatureMapping
    @GetMapping("/blog/blog-update/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('BLOG_BLOG_UPDATE')")
    public ModelAndView getBlogUpdate(@PathVariable String id) {

        final Optional<Blog> optionalBlog = blogRepository.findById(id);

        if (optionalBlog.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/blog/templates/blog-update");

        final BlogUpdate blogUpdate = new BlogUpdate();
        blogUpdate.setTitle(optionalBlog.get().getTitle());
        blogUpdate.setContent(optionalBlog.get().getContent());

        modelAndView.addObject("blog", optionalBlog.get());
        modelAndView.addObject("blogUpdate", blogUpdate);

        return modelAndView;
    }

    @PostMapping("/blog/blog-update/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('BLOG_BLOG_UPDATE')")
    public ModelAndView postBlogUpdate(
            @PathVariable String id,
            @Valid @ModelAttribute("blogUpdate") BlogUpdate blogUpdate,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @CurrentSecurityContext final SecurityContext securityContext) {

        final Optional<Blog> optionalBlog = blogRepository.findById(id);

        if (optionalBlog.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName("com/eachserver/blog/templates/blog-update");
            modelAndView.addObject("blogUpdate", blogUpdate);

            return modelAndView;
        }

        final Blog blog = optionalBlog.get();

        blog.setTitle(blogUpdate.getTitle());
        blog.setContent(blogUpdate.getContent());
        blog.setUpdatedAt(System.currentTimeMillis());
        blog.setUpdatedBy(securityContext.getAuthentication().getName());
        blogRepository.save(blog);

        redirectAttributes.addAttribute("id", id);
        return new ModelAndView("redirect:/blog/blog-view/{id}");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BlogUpdate {

        private String title;

        private String content;
    }
}
