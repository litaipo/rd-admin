/*
 *
 *  *  Copyright (c) 2019-2020, 冷冷 (wangiegie@gmail.com).
 *  *  <p>
 *  *  Licensed under the GNU Lesser General Public License 3.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *  <p>
 *  * https://www.gnu.org/licenses/lgpl.html
 *  *  <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.noriental.modules.generator.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 代码生成器
 *
 * @author Jing.Li
 * @date 2018-07-30
 */
@Mapper
public interface GeneratorMapper {

    /**
     * 分页查询表格
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> queryList(Map<String, Object> map);

    /**
     * 查询表信息
     *
     * @param tableName 表名称
     * @param dsName    数据源名称
     * @return
     */
    @DS("#last")
    Map<String, String> queryTable(@Param("tableName") String tableName, String dsName);

    /**
     * 查询表列信息
     *
     * @param tableName 表名称
     * @param dsName    数据源名称
     * @return
     */
    @DS("#last")
    List<Map<String, String>> queryColumns(@Param("tableName") String tableName, String dsName);
}
