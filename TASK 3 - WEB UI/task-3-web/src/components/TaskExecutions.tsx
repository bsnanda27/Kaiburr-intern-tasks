import React, { useEffect, useState } from "react";
import { Modal, List, Typography, message } from "antd";
import { getExecutions } from "../api/api";

interface Props {
  task: any;
  visible: boolean;
  onClose: () => void;
}

const TaskExecutions: React.FC<Props> = ({ task, visible, onClose }) => {
  const [executions, setExecutions] = useState<any[]>([]);

  useEffect(() => {
    if (task) {
      loadExecutions();
    }
  }, [task]);

  const loadExecutions = async () => {
    try {
      const { data } = await getExecutions(task.id); // ✅ Fetch executions
      setExecutions(data);
    } catch (error) {
      message.error("Failed to load executions");
    }
  };

  return (
    <Modal open={visible} title="Task Executions" onCancel={onClose} footer={null}>
      <List
        dataSource={executions}
        renderItem={(execution) => (
          <List.Item>
            <Typography.Text>
              <b>Start:</b> {execution.startTime} | <b>End:</b> {execution.endTime} | <b>Output:</b> {execution.output}
            </Typography.Text>
          </List.Item>
        )}
      />
    </Modal>
  );
};

export default TaskExecutions;
