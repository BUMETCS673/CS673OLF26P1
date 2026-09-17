// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: WebMvcConfigurer implementation, package wildcard path matching logic
// Human Contributions: Package naming pattern specification, domain structure alignment
// Notes: Adapted package matching predicate to support domain-driven modular package layouts using prefix matching.
// authors: [Your Name/Student ID]

package edu.bu.metcs673.bluejay.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for global path prefixing across domain packages.
 */
// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "How to add a wildcard package matcher to WebMvcConfigurer addPathPrefix for domain-driven package structure"
// AI Contribution: Configuration setup (~70%)
// Modifications:
//  - Implemented WebMvcConfigurer.configurePathMatch
//  - Added package predicate to match all subpackages under the domain root
// Verification:
//  - Verified API routing for controllers across distinct domain packages
// Confidence: High
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Automatically matches edu.bu.metcs673.bluejay.auth, edu.bu.metcs673.bluejay.order, etc.
        configurer.addPathPrefix("/api/v1",
            clazz -> clazz.isAnnotationPresent(RestController.class)
                && clazz.getPackageName().startsWith("edu.bu.metcs673.bluejay"));
    }
}