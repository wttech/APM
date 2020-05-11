package com.cognifide.apm.api.scripts;

import java.util.Date;

public class TransientScript implements MutableScript {

  private final String path;
  private final String content;
  private String checksum;

  private TransientScript(String path, String content) {
    this.path = path;
    this.content = content;
  }

  public static Script create(String path, String content) {
    return new TransientScript(path, content);
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public String getData() {
    return content;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public boolean isLaunchEnabled() {
    return true;
  }

  @Override
  public LaunchMode getLaunchMode() {
    return LaunchMode.ON_DEMAND;
  }

  @Override
  public LaunchEnvironment getLaunchEnvironment() {
    return LaunchEnvironment.ALL;
  }

  @Override
  public String getLaunchHook() {
    return null;
  }

  @Override
  public Date getLaunchSchedule() {
    return null;
  }

  @Override
  public Date getLastExecuted() {
    return null;
  }

  @Override
  public boolean isPublishRun() {
    return false;
  }

  @Override
  public String getChecksum() {
    return checksum;
  }

  @Override
  public String getAuthor() {
    return null;
  }

  @Override
  public Date getLastModified() {
    return null;
  }

  @Override
  public String getReplicatedBy() {
    return null;
  }

  @Override
  public void setValid(boolean flag) {

  }

  @Override
  public void setLastExecuted(Date date) {

  }

  @Override
  public void setPublishRun(boolean flag) {

  }

  @Override
  public void setReplicatedBy(String userId) {

  }

  @Override
  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }
}
