package ac.uk.susx.tag.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

public class WikiTextConverter {
	
	public WikiTextConverter(){}
	
	public String convertToPlainText(String orig) {
		WikiModel wikiModel = new WikiModel("http://www.mywiki.com/wiki/${image}", "http://www.mywiki.com/wiki/${title}");
        String plainStr = wikiModel.render(new PlainTextConverter(), orig);
        
        return cleanString(plainStr);
	}
	
	public String cleanString(String orig){
		String remStart = "";
		Pattern patt = Pattern.compile(remStart);
		Matcher m = patt.matcher(orig);
		System.err.println(orig.substring(m.end()));
		return orig.substring(m.end());
	}

}
