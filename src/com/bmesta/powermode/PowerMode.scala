/*
 * Copyright 2015 Baptiste Mesta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bmesta.powermode

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
  * @author Baptiste Mesta
  */
@State(name = "PowerMode", storages = Array(new Storage(file = "$APP_CONFIG$/power.mode.xml"))) object PowerMode {
  @Nullable def getInstance: PowerMode = {
    ApplicationManager.getApplication.getComponent(classOf[PowerMode])
  }
}

@State(name = "PowerMode", storages = Array(new Storage(file = "$APP_CONFIG$/power.mode.xml"))) class PowerMode extends ApplicationComponent with PersistentStateComponent[PowerMode] {
  private var particleContainerManager = Option.empty[ParticleContainerManager]
  private var enabled: Boolean = true
  private var shakeEnabled: Boolean = true
  def initComponent {
    val editorFactory = EditorFactory.getInstance
    particleContainerManager = Some(new ParticleContainerManager)
    particleContainerManager.foreach(editorFactory.addEditorFactoryListener(_, new Disposable() {
      def dispose {
      }
    }))
    val rawHandler = EditorActionManager.getInstance.getTypedAction.getRawHandler
    EditorActionManager.getInstance.getTypedAction.setupRawHandler(new TypedActionHandler() {
      def execute(@NotNull editor: Editor, c: Char, @NotNull dataContext: DataContext) {
        updateEditor(editor)
        rawHandler.execute(editor, c, dataContext)
      }
    })
  }

  private def updateEditor(@NotNull editor: Editor) {
    particleContainerManager.foreach(_.update(editor))
  }

  def disposeComponent {
    particleContainerManager.foreach(_.dispose)
    particleContainerManager = null
  }

  @NotNull def getComponentName: String = {
    return "PowerMode"
  }

  @Nullable def getState: PowerMode = {
    return this
  }

  def loadState(@NotNull state: PowerMode) {
    XmlSerializerUtil.copyBean(state, this)
  }

  def isEnabled: Boolean = {
      enabled
  }

  def setEnabled(enabled: Boolean) {
    this.enabled = enabled
  }
  def setShakeEnabled(shakeEnabled: Boolean) {
    this.shakeEnabled = shakeEnabled
  }
  def isShakeEnabled: Boolean = {
      shakeEnabled
  }
}