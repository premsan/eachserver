package com.eachserver.blog;

import com.eachserver.application.feature.FeatureMapping;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BlogCreateController {

    private final BlogRepository blogRepository;

    @GetMapping("/blog/blog-create")
    @FeatureMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('BLOG_BLOG_CREATE')")
    public ModelAndView getBlogCreate() {

        ModelAndView model = new ModelAndView("com/eachserver/blog/templates/blog-create");
        model.addObject("blogCreate", new BlogCreate());

        return model;
    }

    @PostMapping("/blog/blog-create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('BLOG_BLOG_CREATE')")
    public ModelAndView postBlogCreate(
            @Valid @ModelAttribute("blogCreate") BlogCreate blogCreate,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @CurrentSecurityContext final SecurityContext securityContext) {

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName("com/eachserver/blog/templates/blog-create");
            modelAndView.addObject("blogCreate", blogCreate);

            return modelAndView;
        }

        final Blog blog =
                blogRepository.save(
                        new Blog(
                                UUID.randomUUID().toString(),
                                null,
                                blogCreate.getTitle(),
                                blogCreate.getContent(),
                                System.currentTimeMillis(),
                                securityContext.getAuthentication().getName()));

        redirectAttributes.addAttribute("id", blog.getId());
        return new ModelAndView("redirect:/blog/blog-view/{id}");
    }

    @Getter
    @Setter
    private static class BlogCreate {

        @NotBlank private String title;

        @NotBlank private String content;
    }
}
