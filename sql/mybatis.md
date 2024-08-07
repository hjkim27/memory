### 1. query 수정 : insert 한 row의 id 를 반환받고자 할 경우
```xml
<!-- 기존 -->
<insert id="insert">/* admin_info.insert */
      insert into admin_info (loginid, passwd, name, ip, phone, email, admin, group_id, part)
      values (#loginid#, #passwd#, #name#, #ip#, #phone#, #email#, #admin#, #groupId#, #part#)
      <selectKey resultClass="java.lang.Integer">
          select currval('admin_info_id_seq')
      </selectKey>
  </insert>

<!-- 수정 -->
<select  id="insert" resultType="int">/* admin_info.insert */
    insert into admin_info (loginid, passwd, name, ip, phone, email, admin, group_id, part)
    values (#{loginid}, #{passwd}, #{name}, #{ip}, #{phone}, #{email}, #{admin}, #{groupId}, #{part})
    RETURNING id;
</select >

<!--  returning 의 경우 postgres 에서만 지원하는 문법으로 returning  말고 select key 를 사용-->
<selectKey keyProperty="id" resultType="java.lang.Integer">
      select currval('s_pcscan_report_id')
</selectKey>
```


### 2. ibatis to mybatis : enum
```xml
<!-- mybatis enum 제거 -->
<!-- ## [ibatis] -->
<sql id="exampleSetClause">
        <iterate conjunction="," prepend="set" property="updateParam">
            $updateParam[].field$ =
            <isEqual property="updateParam[].type" compareValue="EXPR"> $updateParam[].value$ </isEqual>
            <isNotEqual property="updateParam[].type" compareValue="EXPR"> #updateParam[].value# </isNotEqual>
        </iterate>
</sql>

<!-- ## [mybatis] :: 해당변수의 어떤값에서 name() 을 가져온다고 써야 Enum과 String을 비교할 수 있음 -->
 <sql id="exampleSetClause">
        <foreach collection="updateParam" item="item" separator="," open="set ">
            ${item.field} =
                <if test='item.type.name() == "EXPR"'> ${item.value} </if>
                <if test='item.type.name() != "EXPR"'> #{item.value} </if>
        </foreach>
</sql>
```

### 3. 조건문
```xml
<!-- ibatis -->
<isEqual property="noSave" compareValue="false">
</isEqual>


<!-- mybatis -->
<if test="noSave == false">
</if>
```
