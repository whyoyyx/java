package org.nsg.app;

import org.dom4j.Element;

/** �û�������Ϣ�������ӿ� */
public interface UserConfigParser
{
	/** 
	 * ִ�н������� '&lt;user&gt;' �ڵ㿪ʼ��
	 * 
	 *  @param user	: app-config.xml �� '&lt;user&gt;' �ڵ�
	 */
	void parse(Element user);
}
