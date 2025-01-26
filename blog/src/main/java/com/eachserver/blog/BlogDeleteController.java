package com.eachserver.blog;

import com.eachserver.application.feature.FeatureMapping;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BlogDeleteController {

    private final BlogRepository blogRepository;

    @FeatureMapping
    @GetMapping("/blog/blog-delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('BLOG_BLOG_DELETE')")
    public ModelAndView getBlogDelete(final @PathVariable String id) {

        final Optional<Blog> optionalBlog = blogRepository.findById(id);

        if (optionalBlog.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        final ModelAndView model = new ModelAndView("com/eachserver/blog/templates/blog-delete");
        model.addObject("blog", optionalBlog.get());

        return model;
    }

    @PostMapping("/blog/blog-delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('BLOG_BLOG_DELETE')")
    public ModelAndView postBlogDelete(
            final @PathVariable String id, RedirectAttributes redirectAttributes) {

        final Optional<Blog> optionalBlog = blogRepository.findById(id);

        if (optionalBlog.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        blogRepository.delete(optionalBlog.get());

        final ModelAndView model = new ModelAndView("com/eachserver/blog/templates/blog-delete");
        model.addObject("blog", optionalBlog.get());

        return new ModelAndView("redirect:/blog/blog-index");
    }
}
