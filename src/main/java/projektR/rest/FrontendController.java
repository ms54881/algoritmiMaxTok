package projektR.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {
	
    @GetMapping("/{[path:[^\\.]*}")
    public String redirectToIndex() {
        return "forward:/index.html";
    }
    
    @GetMapping("/**/{[path:[^\\.]*}")
    public String redirectToIndexNested() {
        return "forward:/index.html";
    }
}
