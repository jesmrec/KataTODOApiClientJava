/*
 *   Copyright (C) 2016 Karumi.
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.karumi.todoapiclient;

import com.karumi.todoapiclient.dto.TaskDto;
import com.karumi.todoapiclient.exception.TodoApiClientException;
import com.karumi.todoapiclient.exception.ItemNotFoundException;
import com.karumi.todoapiclient.exception.UnknownErrorException;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import com.karumi.todoapiclient.exception.NetworkErrorException;

public class TodoApiClientTest extends MockWebServerTest {

  private TodoApiClient apiClient;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    String mockWebServerEndpoint = getBaseEndpoint();
    apiClient = new TodoApiClient(mockWebServerEndpoint);
  }

  @Test
  public void getTaskListAndCheckTheFirst() throws Exception{

      enqueueMockResponse(200, "getTasksResponse.json");

      List<TaskDto> tasks = apiClient.getAllTasks();

      assertEquals(200, tasks.size());
      assertTaskContainsExpectedValues(tasks.get(0));

  }

  /* GET */
  @Test
  public void requestSentCorrectVerbGetToCorrectPath() throws Exception{

      enqueueMockResponse();
      apiClient.getAllTasks();
      assertGetRequestSentTo("/todos");
  }

  @Test (expected = TodoApiClientException.class)
  public void serverReturnsError418() throws Exception{
      enqueueMockResponse(418);
      apiClient.getAllTasks();
  }

  @Test
  public void requestGetOneTaskToCorrectPathWithCorrectVerb() throws Exception{

    String task = "45";
    enqueueMockResponse();
    apiClient.getTaskById(task);
    assertGetRequestSentTo("/todos/"+task);
  }

  @Test (expected = ItemNotFoundException.class)
  public void requestAnNonExistingItemServerReturns404() throws Exception {

    enqueueMockResponse(404);
    TaskDto task = apiClient.getTaskById("1000");
  }

  @Test (expected = UnknownErrorException.class)
  public void requestAndServerReturns500() throws Exception {

    enqueueMockResponse(500);
    TaskDto task = apiClient.getTaskById("1000");
  }

/* POST */

  @Test
  public void requestSentCorrectVerbPostToCorrectPath() throws Exception {
    enqueueMockResponse();
    apiClient.addTask(new TaskDto("1","1","1",true));
    assertPostRequestSentTo("/todos");
  }

  @Test
  public void checkBodyRequestCorrectInPostWhenCreateNewTask() throws Exception {
    enqueueMockResponse();
    apiClient.addTask(new TaskDto("1","2","Finish this kata",false));
    assertRequestBodyEquals("addTaskRequest.json");
  }

  @Test (expected = TodoApiClientException.class)
  public void ServerReturns418WhenTaskIsCreated() throws Exception {
    enqueueMockResponse(418);
    apiClient.addTask(new TaskDto("1","2","2",false));
  }

  @Test
  public void ServerReturns201WhenTaskIsCreated() throws Exception {
    enqueueMockResponse(201,"addTaskResponse.json");
    TaskDto task = apiClient.addTask(new TaskDto("1","2","Finish this kata",false));
    assertTaskContainsExpectedValues(task);

  }

  @Test (expected = UnknownErrorException.class)
  public void ServerReturns500WhenTaskIsCreated() throws Exception {
    enqueueMockResponse(500);
    apiClient.addTask(new TaskDto("1","2","2",false));
  }

  /* PUT */

  @Test
  public void requestSentCorrectVerbPutToCorrectPath() throws Exception {
    enqueueMockResponse();
    apiClient.updateTaskById(new TaskDto("1","1","sdfds",false));
    assertPutRequestSentTo("/todos/1");
  }

  @Test
  public void checkBodyRequestCorrectInPutWhenCreateNewTask() throws Exception {
    enqueueMockResponse();
    apiClient.updateTaskById(new TaskDto("1","2","Finish this kata",false));
    assertRequestBodyEquals("addTaskRequest.json");
  }

  @Test (expected = TodoApiClientException.class)
  public void ServerReturns418WhenTaskIsUpdated() throws Exception {
    enqueueMockResponse(418);
    apiClient.updateTaskById(new TaskDto("1","2","2",false));
  }

  @Test (expected = UnknownErrorException.class)
  public void ServerReturns500WhenTaskIsUpdated() throws Exception {
    enqueueMockResponse(500);
    apiClient.updateTaskById(new TaskDto("1","2","2",false));
  }

  @Test
  public void ServerReturns201WhenTaskIsUpdated() throws Exception {
    enqueueMockResponse(201,"addTaskResponse.json");
    TaskDto task = apiClient.updateTaskById(new TaskDto("1","2","Finish this kata",false));
    assertTaskContainsExpectedValues(task);

  }


  /* DELETE */

  @Test
  public void ServerReturns200WhenTaskIsDelete() throws Exception {
    enqueueMockResponse(200);
    apiClient.deleteTaskById("1");
  }

  @Test (expected = UnknownErrorException.class)
  public void ServerReturns500WhenTaskIsDeleted() throws Exception {
    enqueueMockResponse(500);
    apiClient.updateTaskById(new TaskDto("1","2","2",false));
  }

  @Test (expected = TodoApiClientException.class)
  public void ServerReturns404WhenTaskIsDelete() throws Exception {
    enqueueMockResponse(404);
    apiClient.updateTaskById(new TaskDto("1","2","2",false));
  }




  private void assertTaskContainsExpectedValues(TaskDto task) {
    assertEquals(task.getId(), "1");
    assertEquals(task.getUserId(), "1");
    assertEquals(task.getTitle(), "delectus aut autem");
    assertFalse(task.isFinished());
  }

}


