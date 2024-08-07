1. query 수정 : insert 한 row의 id 를 반환받고자 할 경우
```sql
-- 기존
<insert id="insert">/* admin_info.insert */
      insert into admin_info (loginid, passwd, name, ip, phone, email, admin, group_id, part)
      values (#loginid#, #passwd#, #name#, #ip#, #phone#, #email#, #admin#, #groupId#, #part#)
      <selectKey resultClass="java.lang.Integer">
          select currval('admin_info_id_seq')
      </selectKey>
  </insert>

-- 수정
<select  id="insert" resultType="int">/* admin_info.insert */
    insert into admin_info (loginid, passwd, name, ip, phone, email, admin, group_id, part)
    values (#{loginid}, #{passwd}, #{name}, #{ip}, #{phone}, #{email}, #{admin}, #{groupId}, #{part})
    RETURNING id;
</select >
```
