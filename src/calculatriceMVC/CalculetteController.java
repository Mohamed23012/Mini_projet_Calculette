package calculatriceMVC;

import java.util.List;

public class CalculetteController implements CalculetteControllerInterface {

    private final CalculatorModelInterface model;
    private final CalculetteView view;

    public CalculetteController(CalculatorModelInterface model, CalculetteView view) {
        this.model = model;
        this.view  = view;

        
        this.view.setController(this);

        // brancher l'observateur sur l'implémentation concrète
        if (model instanceof CalculatorModel m) {
            m.setListener(this);      // notifications automatiques
        } else {
            // Si un autre modèle est fourni, on pourrait gérer autrement (non nécessaire ici)
            view.render(List.of());
        }
    }

    // === notifications modèle -> contrôleur ===
    @Override
    public void change(String accu) {
        // met à jour l'affichage de l'accumulateur
        view.setAccum(accu != null ? accu : "0");
    }

    @Override public void change(List<Double> stackData) {
        view.render(stackData);
    }

    // actions vue -> contrôleur 
    public void onPush() {
        try {
            if (view.hasPendingEntry()) {
                Double v = view.readAndClearEntryOrNull();
                if (v != null) model.push(v);
            }
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
    }

    public void onAdd() { try { onPush(); model.add(); } catch (Exception e) { view.showError(e.getMessage()); } }
    public void onSub() { try { onPush(); model.substract(); } catch (Exception e) { view.showError(e.getMessage()); } }
    public void onMul() { try { onPush(); model.multiply(); } catch (Exception e) { view.showError(e.getMessage()); } }
    public void onDiv() { try { onPush(); model.divide(); } catch (Exception e) { view.showError(e.getMessage()); } }

    public void onOpp() {
        try { onPush(); model.opposite(); }
        catch (Exception e) { view.showError(e.getMessage()); }
    }

    public void onPop()  { try { model.pop(); }  catch (Exception e) { view.showError(e.getMessage()); } }
    public void onDrop() { try { model.drop(); } catch (Exception e) { view.showError(e.getMessage()); } }
    public void onSwap() { try { model.swap(); } catch (Exception e) { view.showError(e.getMessage()); } }
    public void onClear(){ try { model.clear(); }catch (Exception e) { view.showError(e.getMessage()); } }
}
