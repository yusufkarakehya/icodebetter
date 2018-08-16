## iCodeBetter 
iCodeBetter low-code platform is a Spring Boot application. So you need to know some Spring concepts in order to understand codebase better. In this guide we
will give detailed overview of backend and frontend of iCodeBetter :bowtie:

<hr/>

### Backend

<hr/>

#### Controller

> It would be best to read about roles of these requests in [page lifecycle](https://docs.icodebetter.com/lifecycles/page) section of documentation.

*AppServet.java* handles ajax requests. Most common requests are `showPage` , `showForm` , `ajaxQueryData` , `ajaxPostForm` , 
`ajaxExecDbFunc` . So this how it handles these requests:

* `showPage`  

It handles showPage request by calling Framework engine's `getTemplateResult` function and returning template code as response. `showPage` request is used to render a page/grid.   

* `showForm`  

It calls Framework engine's `getFormResult` function and returns that form result as response. `showForm` request is used to load a form template into a page.   

* `ajaxQueryData`  

It calls Framework engine's `executeQuery` function and returns query result as response. `ajaxQueryData` is used to load data of a grid. 

* `ajaxPostForm`  

It calls Framework engine's `postForm4Table` function and returns form result as response. `ajaxPostForm` is used to insert form data into database. 

* `ajaxExecDbFunc`

It calls Framework engine's `executeFunc` function and returns result as response.


#### Framework engine

#### UI adapter


<hr/>
