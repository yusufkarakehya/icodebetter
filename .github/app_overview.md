> NOTE : This section is being updated frequently. Information presented here is correct. We will just keep expanding it to help you learn more parts of the project.

## iCodeBetter 
iCodeBetter low-code platform is a Spring Boot application. So you need to know some Spring concepts in order to understand codebase better. In this guide we
will give detailed overview of backend and frontend of iCodeBetter :bowtie:

<hr/>

### Backend

<hr/>

#### Controller


*AppServet.java* handles ajax requests. Most common requests are `showPage` , `showForm` , `ajaxQueryData` , `ajaxPostForm` , 
`ajaxExecDbFunc` . So this how it handles these requests:

> It would be best to read about roles of these requests in [page lifecycle](https://docs.icodebetter.com/lifecycles/page) section of documentation.

* `showPage` - used to render a page/grid

It handles showPage request by calling Framework engine's `getTemplateResult` function and returning template code as response.    

* `showForm` - used to load a form template into a page

It calls Framework engine's `getFormResult` function and returns that form result as response.  

* `ajaxQueryData` - used to load data of a grid

It calls Framework engine's `executeQuery` function and returns query result as response.  

* `ajaxPostForm` - used to insert form data into database

It calls Framework engine's `postForm4Table` function and returns form result as response. 
* `ajaxExecDbFunc`

It calls Framework engine's `executeFunc` function and returns result as response.


#### Framework engine

As we mentioned above, *AppServet.java* calls framework engine's functions. Basically *FrameworkEngine.java* is the one that orchestrates all the backend: Access Control, CRUD Engine, Query Engine, Conversion Engine, Backend Record State Management Engine, BI Engine.



#### UI adapter

When Framework Engine finishes its job, AppServlet serializes the output of FrameworkEngine using UI Adapters. That output is serialized according to UI library selected in browser. It can be React, Vue or Webix.


