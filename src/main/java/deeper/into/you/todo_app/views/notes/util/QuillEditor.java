package deeper.into.you.todo_app.views.notes.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;

import java.util.concurrent.CompletableFuture;


@Tag("quill-editor")
public class QuillEditor extends Component {

    public QuillEditor() {
        getElement().executeJs("""
            const quill = new Quill(this, {
                theme: 'snow',
                modules: {
                    toolbar: [
                        ['bold', 'italic', 'underline', 'strike'],
                        ['blockquote', 'code-block'],
                        [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                        ['clean']
                    ]
                }
            });
            this.editor = quill;
            
            this.style.height = '100%';
            this.style.overflow = 'auto'; 
            this.style.boxSizing = 'border-box';
            this.editor.root.style.height = '100%';
            this.editor.root.style.overflow = 'auto';
            """);
    }

    public void setValue(String value) {
        getElement().executeJs("this.editor.root.innerHTML = $0", value);
    }

    public CompletableFuture<String> getValue() {
        return getElement().executeJs("return this.editor.root.innerHTML")
                .toCompletableFuture()
                .thenApply(result -> result.asString());
    }

    public Component getComponent() {
        Div wrapper = new Div(this);
        wrapper.setWidth("80%");
        wrapper.setHeight("300px");
        return wrapper;
    }
}
