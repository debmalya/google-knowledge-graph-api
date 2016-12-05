/**
 * Copyright 2015-2016 Debmalya Jash
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deb;

import java.util.Arrays;
import java.util.List;

/**
 * @author debmalyajash
 *
 */
public class Response {
	
	private List<String> resultList;
	private List<String> imageURLs;
	private List<String> urls;
	private List<String[]> typeList;
	private String type;
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 */
	public Response() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getResultList() {
		return resultList;
	}

	public void setResultList(List<String> resultList) {
		this.resultList = resultList;
	}

	

	public List<String> getImageURLs() {
		return imageURLs;
	}

	public void setImageURLs(List<String> imageURLs) {
		this.imageURLs = imageURLs;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	

	/**
	 * @param typeList
	 */
	public void setTypeList(List<String[]> typeList) {
		this.typeList = typeList;
	}

	@Override
	public String toString() {
		return "Response [resultList=" + resultList + ", urls= " + urls + " type = " +  type +"]";
	}

	
}
