SELECT a.first_name, a.last_name, b.salary, c.title, d.dept_no, e.dept_name, f.emp_no
FROM employees a,
     salaries b,
     titles c,
     dept_emp d,
     departments e,
     dept_manager f
WHERE a.emp_no = b.emp_no
  AND b.emp_no = c.emp_no
  AND c.emp_no = d.emp_no
  AND d.dept_no = e.dept_no
  AND e.dept_no = f.dept_no
  AND a.first_name = "Ramzi"
  AND c.title = "Senior Engineer"
  AND b.salary BETWEEN 55025 AND 59700
