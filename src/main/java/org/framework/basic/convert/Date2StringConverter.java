package org.framework.basic.convert;

import org.framework.common.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * 
 *
 * 类名称：Date2StringConverter
 * 类描述：
 * 创建人：chenbo
 * 修改人：chenbo
 * 修改时间：2014年10月28日 下午4:49:20
 * 修改备注：
 * @version 1.0.0
 *
 */
public class Date2StringConverter implements Converter<Date,String>{
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public String convert(Date source) {
		logger.debug("=====================Date2StringConverter InIt===============");
		if(source==null){
			return null;
		}
	    return DateUtils.convert(source, DateUtils.DATE_TIME_FORMAT);
	}

}
