import React from "react";
import { Layout } from "antd";
import TaskList from "./components/TaskList";

const { Header, Content } = Layout;

const App: React.FC = () => (
  <Layout style={{ minHeight: "100vh" }}>
    <Header style={{ color: "white", fontSize: 20 }}>Task Management</Header>
    <Content style={{ padding: "20px" }}>
      <TaskList />
    </Content>
  </Layout>
);

export default App;
