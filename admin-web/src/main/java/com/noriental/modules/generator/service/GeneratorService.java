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

package com.noriental.modules.generator.service;

import com.noriental.common.utils.PageResult;
import com.noriental.modules.generator.entity.GenConfig;
import com.noriental.modules.generator.utils.Query;

/**
 * @author Jing.Li
 * @date 2018/7/29
 */
public interface GeneratorService {
    /**
     * 生成代码
     *
     * @param tableNames 表名称
     * @return
     */
    byte[] generatorCode(GenConfig tableNames);

    /**
     * 分页查询
     *
     * @param query
     * @param dsNameStr
     * @return
     */
    PageResult queryList(Query query, String dsNameStr);
}
