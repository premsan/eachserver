package com.eachserver.blog;

import com.eachserver.application.feature.FeatureMapping;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.asciidoctor.Options;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class BlogViewController {

    private final BlogApplicationContext blogApplicationContext;

    private final BlogRepository blogRepository;

    @FeatureMapping
    @GetMapping("/blog/blog-view/{id}")
    public ModelAndView getBlogView(final @PathVariable String id) {

        final Optional<Blog> optionalBlog = blogRepository.findById(id);

        if (optionalBlog.isEmpty()) {

            return new ModelAndView("com/eachserver/ui/templates/not-found");
        }

        final ModelAndView model = new ModelAndView("com/eachserver/blog/templates/blog-view");

        model.addObject("blog", optionalBlog.get());
        model.addObject(
                "blogContentHtml",
                blogApplicationContext
                        .getAsciidoctor()
                        .convert(optionalBlog.get().getContent(), Options.builder().build()));

        return model;
    }
}
